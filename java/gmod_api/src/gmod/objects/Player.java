package gmod.objects;

import gmod.Lua;


public class Player extends Entity {

	public Player(int index) {
		super(index);
	}

	public int getUniqueID() {
		int ret_val;
		
		Lua.lock();
		{
			Lua.getfield(index, "UniqueID");
			Lua.pushvalue(index);
			Lua.call(1, 1);
			
			ret_val = Lua.tointeger(-1);
			Lua.pop(1);
		}
		Lua.unlock();
		
		return ret_val;
	}
	
}
