package gmod.events;

import gmod.Event;
import gmod.objects.Player;

public class PlayerEvent extends Event {

	protected Player player;
	
	public PlayerEvent() {
		this.player = new Player(1);
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}
	
}
