package gmod.objects;

import gmod.Lua;

public class Entity extends Lua.Object {
	
	protected Entity(int index) {
		super(index);
	}
	
	public Entity() {
		super();
	}

	public static Entity parse(int index) {
		return new Entity(index);
	}
	
	public void activate() {
		Lua.getfield(index, "Activate");
		Lua.pushvalue(index);
		Lua.call(1, 0);
	}
	
	public Angle getAngle() {
		Lua.getfield(index, "Angle");
		Lua.pushvalue(index);
		Lua.call(1, 1);
		return new Angle(Lua.gettop());
	}
	
	public String getClassName() {
		String ret_val;
		
		Lua.getfield(index, "GetClass");
		Lua.pushvalue(index);
		Lua.call(1, 1);
		
		ret_val = Lua.tostring(-1);
		Lua.pop(1);
		
		return ret_val;
	}
	
	public Vector getForward() {
		Lua.getfield(index, "GetForward");
		Lua.pushvalue(index);
		Lua.call(1, 1);
		return new Vector(Lua.gettop());
	}
	
	public String getName() {
		String ret_val;
		
		Lua.getfield(index, "GetName");
		Lua.pushvalue(index);
		Lua.call(1, 1);
		
		ret_val = Lua.tostring(-1);
		Lua.pop(1);
		
		return ret_val;
	}
	
	public Vector getPos() {
		Lua.getfield(index, "GetPos");
		Lua.pushvalue(index);
		Lua.call(1, 1);
		return new Vector(Lua.gettop());
	}
	
	public void setPos(Vector vector) {
		Lua.getfield(index, "SetPos");
		Lua.pushvalue(index);
		Lua.pushvalue(vector.index());
		Lua.call(2, 0);
	}
	
}
