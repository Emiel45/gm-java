package gmod.testing;

import gmod.events.InitializeEvent;

import com.google.common.eventbus.Subscribe;

public class Hooks {
	
	@Subscribe
	public void onInitialize(InitializeEvent e) {
		System.out.println("Gamemode has initialized!");
	}

	
	
}
