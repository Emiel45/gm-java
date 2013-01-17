package gmod.events;

import gmod.Event;
import gmod.Event.Info;
import gmod.objects.Entity;

@Info(name = "OnNPCKilled")
public class NPCKilledEvent extends Event {

	private Entity victim, killer, weapon;
	
	public NPCKilledEvent() {
		this.victim = new Entity(1);
		this.killer = new Entity(2);
		this.weapon = new Entity(3);
	}

	public Entity getVictim() {
		return victim;
	}

	public Entity getKiller() {
		return killer;
	}

	public Entity getWeapon() {
		return weapon;
	}
	
}
