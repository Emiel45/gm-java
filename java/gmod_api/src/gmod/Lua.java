package gmod;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

import static gmod.Lua.Table._G;

public class Lua {

	static {
		System.loadLibrary("gmsv_java_win32");
	}

	public static interface Function {

		public int invoke(int nargs, int nresults);

	}

	public static class Ref<T extends Object> {
		
		private static final Random random = new Random();
		
		private int id;
		private Class<? extends Object> objClass;
		
		public Ref(T obj) {
			this.id = random.nextInt();
			this.objClass = obj.getClass();
			
			Lua.Table java = _G.getFieldTable("java");
			java.invokeVoid("setref", id, obj);
			java.pop();
		}
		
		@SuppressWarnings("unchecked")
		public T get() {
			Lua.Table java = _G.getFieldTable("java");
			java.invokeVoid("getref", id);
			Lua.remove(java.index);

			try {
				Constructor<? extends Object> constructor = objClass.getConstructor(int.class);
				return (T) constructor.newInstance(Lua.gettop());
			} catch (java.lang.Exception e) {
				e.printStackTrace();
			}
			
			return null;
		}
		
	}
	
	public static class Object {

		protected int index;

		public Object(int index) {
			this.index = Lua.absindex(index);
		}

		public boolean getFieldBoolean(String name) {
			boolean ret_value;
			Lua.getfield(index, name);
			ret_value = Lua.toboolean(-1);
			Lua.pop(1);
			return ret_value;
		}

		public int getFieldInteger(String name) {
			int ret_value;
			Lua.getfield(index, name);
			ret_value = Lua.tointeger(-1);
			Lua.pop(1);
			return ret_value;
		}

		public double getFieldNumber(String name) {
			double ret_value;
			Lua.getfield(index, name);
			ret_value = Lua.tointeger(-1);
			Lua.pop(1);
			return ret_value;
		}

		public String getFieldString(String name) {
			String ret_value;
			Lua.getfield(index, name);
			ret_value = Lua.tostring(-1);
			Lua.pop(1);
			return ret_value;
		}

		public Object getFieldObject(String name) {
			Lua.getfield(index, name);
			return new Object(Lua.gettop());
		}

		public Table getFieldTable(String name) {
			Lua.getfield(index, name);
			return new Table(Lua.gettop());
		}

		public void setField(String name, boolean b) {
			Lua.pushboolean(b);
			Lua.setfield(index, name);
		}

		public void setField(String name, int i) {
			Lua.pushinteger(i);
			Lua.setfield(index, name);
		}

		public void setField(String name, double n) {
			Lua.pushnumber(n);
			Lua.setfield(index, name);
		}

		public void setField(String name, String str) {
			Lua.pushstring(str);
			Lua.setfield(index, name);
		}

		public void setField(String name, Object obj) {
			Lua.pushvalue(obj.index);
			Lua.setfield(index, name);
		}

		public void setField(String name, java.lang.Object obj) {
			Lua.pushobject(obj);
			Lua.setfield(index, name);
		}

		public int invoke(int nresults, String name, java.lang.Object... args) {
			/* store top */
			int top = Lua.gettop();
			
			/* push function on the stack */
			Lua.getfield(index, name);
			
			/* push args on the stack */
			for (int i = 0; i < args.length; i++) {
				java.lang.Object arg = args[i];

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
					Lua.pushvalue(((Object) arg).index);
					continue;
				}

				if (Function.class.isInstance(arg)) {
					Lua.pushfunction((Function) arg);
					continue;
				}
				
				throw new Error("Invalid argument (" + i + ") of " + arg.getClass());
			}
			
			Lua.call(args.length, nresults);
			
			return Lua.gettop() - top;
		}

		public int call(int nresults, String name, java.lang.Object... args) {
			java.lang.Object[] newArgs = new java.lang.Object[args.length + 1];
			System.arraycopy(args, 0, newArgs, 1, args.length);
			newArgs[0] = this;
			return this.invoke(nresults, name, newArgs);
		}
		
		public void invokeVoid(String name, java.lang.Object... args) {
			this.invoke(0, name, args);
		}
		
		public boolean invokeBoolean(String name, java.lang.Object... args) {
			boolean ret_val;
			this.invoke(1, name, args);
			ret_val = Lua.toboolean(-1);
			Lua.pop(1);
			return ret_val;
		}
		
		public int invokeInteger(String name, java.lang.Object... args) {
			int ret_val;
			this.invoke(1, name, args);
			ret_val = Lua.tointeger(-1);
			Lua.pop(1);
			return ret_val;
		}
		
		public double invokeNumber(String name, java.lang.Object... args) {
			double ret_val;
			this.invoke(1, name, args);
			ret_val = Lua.tonumber(-1);
			Lua.pop(1);
			return ret_val;
		}
		
		public String invokeString(String name, java.lang.Object... args) {
			String ret_val;
			this.invoke(1, name, args);
			ret_val = Lua.tostring(-1);
			Lua.pop(1);
			return ret_val;
		}
		
		public Lua.Object invokeObject(String name, java.lang.Object... args) {
			this.invoke(1, name, args);
			return new Lua.Object(Lua.gettop());
		}
        
        public void callVoid(String name, java.lang.Object... args) {
            this.call(0, name, args);
        }
        
        public boolean callBoolean(String name, java.lang.Object... args) {
            boolean ret_val;
            this.call(1, name, args);
            ret_val = Lua.toboolean(-1);
            Lua.pop(1);
            return ret_val;
        }
        
        public int callInteger(String name, java.lang.Object... args) {
            int ret_val;
            this.call(1, name, args);
            ret_val = Lua.tointeger(-1);
            Lua.pop(1);
            return ret_val;
        }
        
        public double callNumber(String name, java.lang.Object... args) {
            double ret_val;
            this.call(1, name, args);
            ret_val = Lua.tonumber(-1);
            Lua.pop(1);
            return ret_val;
        }
        
        public String callString(String name, java.lang.Object... args) {
            String ret_val;
            this.call(1, name, args);
            ret_val = Lua.tostring(-1);
            Lua.pop(1);
            return ret_val;
        }
        
        public Lua.Object callObject(String name, java.lang.Object... args) {
            this.call(1, name, args);
            return new Lua.Object(Lua.gettop());
        }

		@Override
		public String toString() {
			String ret_val;

			Table._G.invoke(1, "tostring",this);
			ret_val = Lua.tostring(-1);
			Lua.pop(1);

			return ret_val;
		}

		public void pop() {
			if(index != Lua.gettop()) {
				throw new Error("Object is not on top of the stack! (" + index + " != " + Lua.gettop() + ")");
			}
			Lua.pop(1);
			this.index = 0;
		}
		
		public int index() {
			return index;
		}

	}

	public static class Table extends Object {

		public static final Table _G = new Table(GLOBALSINDEX);

		public Table(int index) {
			super(index);
		}

	}
	
	// TODO Array<T> (seriously, do it)
	public static class Array extends Table implements Collection<Object> {

		private class ArrayIterator implements Iterator<Object> {

			private int pos = 0;
			private Array array;
			
			public ArrayIterator(Array array) {
				this.array = array;
			}
			
			@Override
			public boolean hasNext() {
				return pos < array.size();
			}

			@Override
			public Object next() {
				pos++;
				Lua.pushinteger(pos);
				Lua.gettable(array.index());
				return new Object(Lua.gettop());
			}

			@Override
			public void remove() {
				// TODO implement this?
				throw new UnsupportedOperationException();
			}
			
		}
		
		public Array(int index) {
			super(index);
		}

		@Override
		public int size() {
			return Lua.objlen(index);
		}

		@Override
		public boolean isEmpty() {
			return this.size() == 0;
		}

		@Override
		public boolean contains(java.lang.Object o) {
			// TODO implement this?
			throw new UnsupportedOperationException();
		}

		@Override
		public Iterator<Object> iterator() {
			return new ArrayIterator(this);
		}
		
		@Override
		public <T> T[] toArray(T[] a) {
			// TODO implement this?
			throw new UnsupportedOperationException();
		}

		@Override
		public java.lang.Object[] toArray() {
			// TODO implement this?
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean add(Object e) {
			// TODO implement this?
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean remove(java.lang.Object o) {
			// TODO implement this?
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean containsAll(Collection<?> c) {
			// TODO implement this?
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean addAll(Collection<? extends Object> c) {
			// TODO implement this?
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean removeAll(Collection<?> c) {
			// TODO implement this?
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean retainAll(Collection<?> c) {
			// TODO implement this?
			throw new UnsupportedOperationException();
		}

		@Override
		public void clear() {
			// TODO implement this?
			throw new UnsupportedOperationException();
		}
	}

	@SuppressWarnings("serial")
	public static class Exception extends java.lang.Exception {

		public Exception(String message) {
			super(message);
		}

	}

	public static final int REGISTRYINDEX = -10000;
	public static final int GLOBALSINDEX = -10002;

	public static int upvalueindex(int index) {
		return GLOBALSINDEX - index;
	}

	public static int absindex(int index) {
		return (index > 0 || index <= REGISTRYINDEX) ? (index) : (Lua.gettop() + index + 1);
	}

	public static native void lock();

	public static native void unlock();

	public static native void getfield(int index, String name);

	public static native void setfield(int index, String name);

	public static void getglobal(String name) {
		getfield(GLOBALSINDEX, name);
	}

	public static void setglobal(String name) {
		setfield(GLOBALSINDEX, name);
	}

	public static native void gettable(int index);

	public static native int gettop();
	
	public static native void settop(int index);

	public static native void remove(int index);

	public static native void pushvalue(int index);

	public static native void pushnil();

	public static native void pushboolean(boolean b);

	public static native void pushinteger(int i);

	public static native void pushnumber(double n);

	public static native void pushstring(String str);

	public static native void pushobject(java.lang.Object obj);

	public static native void pushfunction(Function f);

	public static native void pushclosure(Function f, int nargs);

	public static void pop(int n) {
		settop(-n - 1);
	}

	public static native void call(int nargs, int nresults);

	public static native boolean toboolean(int index);

	public static native int tointeger(int index);

	public static native double tonumber(int index);

	public static native String tostring(int index);

	public static native java.lang.Object toobject(int index);

	public static native int objlen(int index);

	public static native void dump_stack();

}
