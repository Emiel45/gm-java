package gmod.objects;

public class Player extends Entity {

	public Player(int index) {
		super(index);
	}

	public int getUniqueID() {
		return super.callInteger("UniqueID");
	}
	
	public void chatPrint(String message) {
		super.callVoid("ChatPrint", message);
	}
	
}
