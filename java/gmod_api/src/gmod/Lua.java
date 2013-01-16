package gmod;

public class Lua {

	static {
		System.loadLibrary("gmsv_java_win32");
	}

	public static interface Function {

		public int invoke(int nargs, int nresults);

	}

	private static class LuaFunction extends Object implements Function {

		public LuaFunction(int index) {
			super(index);
		}

		@Override
		public int invoke(int nargs, int nresults) {
			int top = Lua.gettop();
			Lua.call(nargs, nresults);
			return Lua.gettop() - top;
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

		public Function getFieldFunction(String name) {
			Lua.getfield(index, name);
			return new LuaFunction(Lua.gettop());
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
			Function f = this.getFieldFunction(name);

			for (int i = 0; i < args.length; i++) {
				java.lang.Object arg = args[i];
				Class<?> argClass = arg.getClass();

				if (argClass == Boolean.class) {
					Lua.pushboolean((Boolean) arg);
					continue;
				}

				if (argClass == Integer.class) {
					Lua.pushinteger((Integer) arg);
					continue;
				}

				if (argClass == Double.class) {
					Lua.pushnumber((Double) arg);
					continue;
				}

				if (argClass == String.class) {
					Lua.pushstring((String) arg);
					continue;
				}

				if (argClass == Function.class) {
					Lua.pushfunction((Function) arg);
					continue;
				}

				if (argClass == Object.class) {
					Lua.pushvalue(((Object) arg).index);
					continue;
				}
				
				throw new Error("Invalid argument (" + i + ")");
			}
			return f.invoke(args.length, nresults);
		}
		
		public int invoke(String name, java.lang.Object... args) {
			return this.invoke(name, -1, args);
		}

		public int call(int nresults, String name, java.lang.Object... args) {
			java.lang.Object[] newArgs = new java.lang.Object[args.length + 1];
			System.arraycopy(args, 0, newArgs, 1, args.length);
			newArgs[0] = this;
			return this.invoke(name, nresults, newArgs);
		}

		public int call(String name, java.lang.Object... args) {
			return this.call(name, -1, args);
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

	public static native int gettop();

	public static native void pushvalue(int index);

	public static native void pushnil();

	public static native void pushboolean(boolean b);

	public static native void pushinteger(int i);

	public static native void pushnumber(double n);

	public static native void pushstring(String str);

	public static native void pushobject(java.lang.Object obj);

	public static native void pushfunction(Function f);

	public static native void pushclosure(Function f, int nargs);

	public static native void settop(int index);

	public static void pop(int n) {
		settop(-n - 1);
	}

	public static native void call(int nargs, int nresults);

	public static native boolean toboolean(int index);

	public static native int tointeger(int index);

	public static native double tonumber(int index);

	public static native String tostring(int index);

	public static native java.lang.Object toobject(int index);

	public static native void dump_stack();

}
