package gmod;

import gmod.libraries.Hook;

import java.lang.reflect.Method;
import java.util.Map;

import com.google.common.collect.Maps;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

public class Hooks {

	private static final EventBus eventBus = new EventBus();
	private static final Map<String, Handler> eventHandlers = Maps.newHashMap();

	public static void register(Object o) {
		eventBus.register(o);

		for (Method m : o.getClass().getMethods()) {
			if (!m.isAnnotationPresent(Subscribe.class))
				continue;

			if (m.getParameterTypes().length != 1)
				continue;

			Class<?> parmClass = m.getParameterTypes()[0];
			if (!Event.class.isAssignableFrom(parmClass))
				continue;

			Class<? extends Event> eventClass = parmClass.asSubclass(Event.class);
			if (!eventClass.isAnnotationPresent(Event.Info.class))
				continue;

			Event.Info eventInfo = eventClass.getAnnotation(Event.Info.class);
			if (!eventHandlers.containsKey(eventInfo.name())) {
				Handler eventHandler = new Handler(eventClass);
				eventHandlers.put(eventInfo.name(), eventHandler);
				
				Hook hook = Library.Factory.get(Hook.class);
				hook.add(eventInfo.name(), "java." + eventClass.getName(), eventHandler);
				hook.pop();
			}
		}
	}

	public static void unregister(Object o) {
		eventBus.unregister(o);
	}

	private static class Handler implements Lua.Function {

		private Class<? extends Event> eventClass;
		
		public Handler(Class<? extends Event> eventClass) {
			this.eventClass = eventClass;
		}
		
		@Override
		public int invoke(int nargs, int nresults) {
			Event event;
			try {
				event = eventClass.newInstance();
				eventBus.post(event);
				
				Object[] retVals = event.getReturnValues();
				
				/* push return values on the stack */
				for (int i = 0; i < retVals.length; i++) {
					Object arg = retVals[i];

					if (Boolean.class.isInstance(arg)) {
						Lua.pushboolean((Boolean) arg);
						continue;
					}

					if (Integer.class.isInstance(arg)) {
						Lua.pushinteger((Integer) arg);
						continue;
					}

					if (Double.class.isInstance(arg)) {
						Lua.pushnumber((Double) arg);
						continue;
					}

					if (String.class.isInstance(arg)) {
						Lua.pushstring((String) arg);
						continue;
					}

					if (Object.class.isInstance(arg)) {
						Lua.pushvalue(((Lua.Object) arg).index);
						continue;
					}

					if (Lua.Function.class.isInstance(arg)) {
						Lua.pushfunction((Lua.Function) arg);
						continue;
					}
					
					throw new Error("Invalid argument (" + i + ") of " + arg.getClass());
				}
				
				return retVals.length;
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}

			return 0;
		}

	}

}
