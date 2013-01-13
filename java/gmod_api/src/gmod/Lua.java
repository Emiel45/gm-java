package gmod;

public class Lua {

	static {
		System.loadLibrary("gmsv_java_win32");
	}

	public static interface Function {
		
		public int invoke() throws Exception;
		
	}
	
	public static abstract class Object {

		protected int index;
		
		public Object(int index) {
			this.index = index;
		}
		
		public Object() {
			this.index = Lua.gettop();
		}
		
		@Override
		public String toString() {
			String ret_val;
			
			Lua.lock();
			{
				Lua.getglobal("tostring");
				Lua.pushvalue(index);
				Lua.call(1, 1);
				
				ret_val = Lua.tostring(-1);
			}
			Lua.unlock();
			
			return ret_val;
		}
		
		public int index() {
			return index;
		}
		
	}
	
	public static final int GLOBALSINDEX = -10002;
	
	public static int upvalueindex(int index) {
		return GLOBALSINDEX - index;
	}
	
	public static native void lock();
	public static native void unlock();
	
	public static native void getfield(int index, String name);
	public static void getglobal(String name) {	getfield(GLOBALSINDEX, name); }
	
	public static native void setfield(int index, String name);
	public static void setglobal(String name) {	setfield(GLOBALSINDEX, name); }

	public static native int gettop();
	
	public static native void pushvalue(int index);
	public static native void pushboolean(boolean b);
	public static native void pushnumber(double n);
	public static native void pushstring(String str);
	public static native void pushobject(java.lang.Object obj);
	public static native void pushfunction(Function f);
	public static native void pushclosure(Function f, int nargs);
	
	public static native void settop(int index);
	public static void pop(int n) { settop(-n - 1); }
	
	public static native void call(int nargs, int nresults);
	
	public static native double tonumber(int index);
	public static native String tostring(int index);
	public static native java.lang.Object toobject(int index);
	
	public static native void dump_stack();
	
}
