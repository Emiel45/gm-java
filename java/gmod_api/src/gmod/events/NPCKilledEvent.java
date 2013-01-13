package gmod.events;

import gmod.Entity;
import gmod.Event;
import gmod.Lua;

@Event.Info(name = "OnNPCKilled")
public class NPCKilledEvent extends Event {

	private Entity victim, killer, weapon;
	
	@Override
	public void parse() {
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
