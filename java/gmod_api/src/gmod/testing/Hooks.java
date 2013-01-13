package gmod.testing;

import gmod.Entities;
import gmod.events.InitializeEvent;
import gmod.events.NPCKilledEvent;
import gmod.objects.Entity;
import gmod.objects.Vector;

import com.google.common.eventbus.Subscribe;

public class Hooks {

	@Subscribe
	public void onInitialize(InitializeEvent e) {
		System.out.println("Gamemode has initialized!");
	}

	@Subscribe
	public void onNPCKilled(NPCKilledEvent e) {
		System.out.println(e.getKiller().getName() + " killed " + e.getVictim().getClassName() + " with a " + e.getWeapon().getClassName());
		
		Entity ent = e.getVictim();
		Entity killer = e.getKiller();
		
		Entity newEnt = Entities.create(ent.getClassName());
		System.out.println("Created new entity");
		
		Vector newPos = new Vector(killer.getPos());
		System.out.println("Cloned pos");
		
		newPos.add(new Vector(0, 0, 100));
		System.out.println("Added 100 z to pos");
		
		newEnt.setPos(newPos);
		System.out.println("Set pos");
	}

}
