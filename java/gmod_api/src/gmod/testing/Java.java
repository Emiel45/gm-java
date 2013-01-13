package gmod.testing;

import gmod.Hook;

public class Java {

	public static void main(String[] args) {
		Hook.init();
		
		Hook.register(new Hooks());
	}

}
