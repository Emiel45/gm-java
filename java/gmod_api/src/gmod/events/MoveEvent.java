package gmod.events;

import gmod.Event;
import gmod.Event.Info;
import gmod.objects.MoveData;
import gmod.objects.Player;

@Info(name = "Move")
public class MoveEvent extends Event {

	private Player player;
	private MoveData moveData;
	
	public MoveEvent() {
		this.player = new Player(1);
		this.moveData = new MoveData(2);
	}

	public Player getPlayer() {
		return player;
	}

	public MoveData getMoveData() {
		return moveData;
	}
	
}
