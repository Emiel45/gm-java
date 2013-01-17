package gmod.libraries;

import gmod.Library;
import gmod.Library.Info;
import gmod.Lua;

@Info(name = "hook")
public interface Hook extends Library {

	@Info(name = "Add") public void add(String hookName, Object identifier, Lua.Function func);
	
}
