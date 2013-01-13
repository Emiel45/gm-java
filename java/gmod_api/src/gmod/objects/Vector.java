package gmod.objects;

import gmod.Lua;

public class Vector extends Lua.Object {
	
	public Vector(int index) {
		super(index);
	}
	
	public Vector(double x, double y, double z) {
		Lua.getglobal("Vector");
		Lua.pushnumber(x);
		Lua.pushnumber(y);
		Lua.pushnumber(z);
		Lua.call(3, 1);
		this.index = Lua.gettop();
	}
	
	public Vector(Vector vector) {
		this(vector.getX(), vector.getY(), vector.getZ());
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
	
	public double getY() {
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

	public void setY(double y) {
		Lua.lock();
		{
			Lua.pushnumber(y);
			Lua.setfield(index, "y");
		}
		Lua.unlock();
	}
	
	public double getZ() {
		double ret_val;
		
		Lua.lock();
		{
			Lua.getfield(index, "z");
			ret_val = Lua.tonumber(-1);
			Lua.pop(1);
		}
		Lua.unlock();
		
		return ret_val;
	}

	public void setZ(double z) {
		Lua.lock();
		{
			Lua.pushnumber(z);
			Lua.setfield(index, "z");
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
