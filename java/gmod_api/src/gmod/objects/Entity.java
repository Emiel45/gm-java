package gmod.objects;

import gmod.Lua;

public class Entity extends Lua.Object {
	
	public Entity(int index) {
		super(index);
	}

	public String getClassName() {
		return super.callString("GetClass");
	}

	public String getName() {
		return super.callString("GetName");
	}
	
	public Vector getPos() {
		return new Vector(super.callObject("GetPos").index());
	}
	
	public void setPos(Vector pos) {
		super.callVoid("SetPos", pos);
	}
	
}
