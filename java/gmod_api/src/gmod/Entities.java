package gmod;

import gmod.objects.Entity;

public class Entities {

	public static Entity create(String className) {
		Entity ret_val;
		
		Lua.getglobal("ents");
		Lua.getfield(-1, "Create");
		Lua.pushstring(className);
		Lua.call(1, 1);
		ret_val = new Entity();
		
		return ret_val;
	}
	
}
