package gmod.testing;

import gmod.events.InitializeEvent;
import gmod.events.PlayerSpawnEvent;

import com.google.common.eventbus.Subscribe;

public class Hooks {
	
	@Subscribe
	public void onInitialize(InitializeEvent e) {
		System.out.println("Gamemode has initialized!");
	}
	
	@Subscribe
	public void onPlayerSpawn(PlayerSpawnEvent e) {
		System.out.println(e.getPlayer().getName() + " spawned.");
		e.getPlayer().chatPrint("PENIS :D");
	}
	
}
