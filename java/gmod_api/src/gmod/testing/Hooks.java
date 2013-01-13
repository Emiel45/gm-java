package gmod.testing;

import gmod.events.NPCKilledEvent;
import gmod.objects.Entity;

import com.google.common.eventbus.Subscribe;

public class Hooks {

	@Subscribe
	public void onInitialize() {
		System.out.println("Gamemode has initialized!");
	}

	@Subscribe
	public void onNPCKilled(NPCKilledEvent e) {
		System.out.println("NPCKilled!");
		System.out.println(e.getKiller().getName() + " killed " + e.getVictim().getClassName() + " with a " + e.getWeapon().getClassName());
		
		Entity ent = e.getVictim();
	}

}
