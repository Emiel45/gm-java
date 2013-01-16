package gmod.objects;

import gmod.Lua;

import static gmod.Lua.Table._G;

public class Angle extends Lua.Object {

	public Angle(int index) {
		super(index);
	}
	
	public Angle(double p, double y, double r) {
		super(_G.invokeObject("Angle", p, y, r).index());
	}
	
	public double getPitch() {
		return super.getFieldNumber("p");
	}
	
	public void setPitch(double p) {
		super.setField("p", p);
	}
	
	public double getYaw() {
		return super.getFieldNumber("y");
	}
	
	public void setYaw(double y) {
		super.setField("y", y);
	}
	
	public double getRoll() {
		return super.getFieldNumber("r");
	}
	
	public void setRoll(double r) {
		super.setField("r", r);
	}

}
