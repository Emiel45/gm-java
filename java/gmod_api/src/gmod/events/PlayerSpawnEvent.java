package gmod.events;

import gmod.Event;
import gmod.objects.Player;

@Event.Info(name = "PlayerSpawn")
public class PlayerSpawnEvent extends Event {

	private Player player;
	
	public PlayerSpawnEvent() {
		this.player = new Player(1);
	}

	public Player getPlayer() {
		return player;
	}
	
}
