package gmod;

public class Entity extends Lua.Object {
	
	protected Entity(int index) {
		super(index);
	}
	
	public static Entity parse(int index) {
		return new Entity(index);
	}
	
	public String getClassName() {
		String ret_val;
		
		Lua.lock();
		{
			Lua.getfield(index, "GetClass");
			Lua.pushvalue(index);
			Lua.call(1, 1);
			
			ret_val = Lua.tostring(-1);
		}
		Lua.unlock();
		
		return ret_val;
	}
	
	public String getName() {
		String ret_val;
		
		Lua.lock();
		{
			Lua.getfield(index, "GetName");
			Lua.pushvalue(index);
			Lua.call(1, 1);
			
			ret_val = Lua.tostring(-1);
		}
		Lua.unlock();
		
		return ret_val;
	}
	
}
