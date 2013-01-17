package gmod.events;

import gmod.Event.Info;
import gmod.objects.Entity;

@Info(name = "PlayerSelectSpawn")
public class PlayerSelectSpawnEvent extends PlayerEvent {

	public void setSpawn(Entity e) {
		super.setReturnValues(e);
	}
	
}
