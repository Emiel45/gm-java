package gmod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

import com.google.common.collect.Maps;
import com.google.common.primitives.Primitives;

import static gmod.Lua.Table._G;

public interface Library {

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.TYPE, ElementType.METHOD })
	public static @interface Info {

		public String name();
		
	}

	public static class Factory {

		@SuppressWarnings("unchecked")
		public static <T extends Library> T get(Class<T> libraryClass) {
			return (T) Proxy.newProxyInstance(libraryClass.getClassLoader(), new Class[] { libraryClass }, new Handler(libraryClass));
		}

	}

	public void pop();
	
	static class Handler implements InvocationHandler {

		private String name;
		private Lua.Table lib;

		private Map<Method, String> methodNames = Maps.newHashMap();

		public Handler(Class<? extends Library> libraryClass) {
			this.name = libraryClass.getAnnotation(Info.class).name();
			this.lib = _G.getFieldTable(name);

			for (Method m : libraryClass.getMethods()) {
				if (!m.isAnnotationPresent(Info.class)) {
					continue;
				}

				methodNames.put(m, m.getAnnotation(Info.class).name());
			}
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			if(method.getName().equals("pop")) {
				lib.pop();
				return null;
			}
			
			if(args == null) args = new Object[0];
			
			Class<?> returnType = method.getReturnType();
			if(Primitives.allPrimitiveTypes().contains(returnType)) {
				returnType = Primitives.wrap(returnType);
			}
			
			String name = methodNames.get(method);
			if(name == null) {
				name = method.getName();
				name = Character.toLowerCase(name.charAt(0)) + name.substring(1);
			}
			
			if(Void.class.isAssignableFrom(returnType)) {
				lib.invokeVoid(name, args);
				return null;
			}

			if(Boolean.class.isAssignableFrom(returnType)) {
				return lib.invokeBoolean(name, args);
			}

			if(Integer.class.isAssignableFrom(returnType)) {
				return lib.invokeInteger(name, args);
			}

			if(Double.class.isAssignableFrom(returnType)) {
				return lib.invokeNumber(name, args);
			}
			
			if(String.class.isAssignableFrom(returnType)) {
				return lib.invokeString(name, args);
			}

			if(Lua.Object.class.isAssignableFrom(returnType)) {
				Lua.Object obj = lib.invokeObject(name, args);
				
				Constructor<?> constructor = returnType.getConstructor(int.class);
				return constructor.newInstance(obj.index());
			}
			
			throw new Exception("Invalid return type");
		}

	}

}
