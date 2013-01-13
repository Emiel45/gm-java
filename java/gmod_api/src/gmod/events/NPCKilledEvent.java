package gmod.events;

import gmod.Event;
import gmod.Lua;
import gmod.objects.Entity;

@Event.Info(name = "OnNPCKilled")
public class NPCKilledEvent extends Event {

	private Entity victim, killer, weapon;
	
	public NPCKilledEvent() {
		Lua.lock();
		{
			this.victim = Entity.parse(1);
			this.killer = Entity.parse(2);
			this.weapon = Entity.parse(3);
			
			Lua.getglobal("print");
			Lua.pushvalue(1);
			Lua.pushvalue(2);
			Lua.pushvalue(3);
			Lua.call(3, 0);
		}
		Lua.unlock();
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
