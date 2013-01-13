package gmod.objects;

import gmod.Lua;

public class Angle extends Lua.Object {

	public Angle(int index) {
		super(index);
	}
	
	public double getPitch() {
		double ret_val;
		
		Lua.lock();
		{
			Lua.getfield(index, "p");
			ret_val = Lua.tonumber(-1);
			Lua.pop(1);
		}
		Lua.unlock();
		
		return ret_val;
	}
	
	public double getYaw() {
		double ret_val;
		
		Lua.lock();
		{
			Lua.getfield(index, "y");
			ret_val = Lua.tonumber(-1);
			Lua.pop(1);
		}
		Lua.unlock();
		
		return ret_val;
	}
	
	public double getRoll() {
		double ret_val;
		
		Lua.lock();
		{
			Lua.getfield(index, "r");
			ret_val = Lua.tonumber(-1);
			Lua.pop(1);
		}
		Lua.unlock();
		
		return ret_val;
	}

}
