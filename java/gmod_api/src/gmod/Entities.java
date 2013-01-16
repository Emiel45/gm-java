package gmod;

import gmod.objects.Entity;

import static gmod.Lua.Table._G;

public class Entities {

	public static Entity create(String className) {
		return new Entity(_G.getFieldTable("ents").invokeObject("Create", className).index());
	}
	
	public static Lua.Array getAll() {
		return new Lua.Array(_G.getFieldTable("ents").invokeObject("GetAll").index());
	}
	
}
