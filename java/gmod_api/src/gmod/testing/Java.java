package gmod.testing;

import gmod.lua.Hook;

public class Java {

	public static void main(String[] args) {
		Hook.register(new Hooks());
	}

}
