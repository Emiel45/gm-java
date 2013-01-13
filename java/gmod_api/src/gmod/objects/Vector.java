package gmod.objects;

import gmod.Lua;

public class Vector extends Lua.Object {
	
	// sec
	
	public Vector(int index) {
		super(index);
	}
	
	public double getX() {
		double ret_val;
		
		Lua.lock();
		{
			Lua.getfield(index, "x");
			ret_val = Lua.tonumber(-1);
			Lua.pop(1);
		}
		Lua.unlock();
		
		return ret_val;
	}

	public void setX(double x) {
		Lua.lock();
		{
			Lua.pushnumber(x);
			Lua.setfield(index, "x");
		}
		Lua.unlock();
	}
	
	public void add(Vector vector) {
		Lua.lock();
		{
			Lua.getfield(index, "Add");
			Lua.pushvalue(index);
			Lua.pushvalue(vector.index);
			Lua.call(2, 0);
		}
		Lua.unlock();
	}
	
	public double distance(Vector vector) {
		double ret_val;
		
		Lua.lock();
		{
			Lua.getfield(index, "Distance");
			Lua.pushvalue(index);
			Lua.pushvalue(vector.index);
			Lua.call(2, 1);
			ret_val = Lua.tonumber(-1);
			Lua.pop(1);
		}
		Lua.unlock();
		
		return ret_val;
	}
	
	public void mul(double value) {
		Lua.lock();
		{
			Lua.getfield(index, "Mul");
			Lua.pushvalue(index);
			Lua.pushnumber(value);
			Lua.call(2, 0);
		}
		Lua.unlock();
	}
	
}
