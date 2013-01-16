package gmod.objects;

import gmod.Lua;

import static gmod.Lua.Table._G;

public class Vector extends Lua.Object {
	
	public Vector(int index) {
		super(index);
	}
	
	public Vector(double x, double y, double z) {
		super(_G.invokeObject("Vector", x, y, z).index());
	}
	
	public Vector(Vector vector) {
		this(vector.getX(), vector.getY(), vector.getZ());
	}
	
	public double getX() {
		return super.getFieldNumber("x");
	}

	public void setX(double x) {
		super.setField("x", x);
	}
	
	public double getY() {
		return super.getFieldNumber("y");
	}

	public void setY(double y) {
		super.setField("y", y);
	}
	
	public double getZ() {
		return super.getFieldNumber("Z");
	}

	public void setZ(double z) {
		super.setField("z", z);
	}
	
	public void add(Vector v) {
		super.call("Add", v);
	}
	
	public Angle angle() {
		return new Angle(super.callObject("Angle").index());
	}
	
}
