package gmod.libraries;

import gmod.Library;
import gmod.Lua;
import gmod.Library.Info;

@Info(name = "ents")
public interface Entities extends Library {
	
	@Info(name = "GetAll")	public Lua.Array getAll();
	
}
