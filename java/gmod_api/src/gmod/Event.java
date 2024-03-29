package gmod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public abstract class Event {
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public static @interface Info {
		
		public String name();
		
	}
	
	private Object[] returnValues = new Object[0];

	public Object[] getReturnValues() {
		return returnValues;
	}

	public void setReturnValues(Object... returnValues) {
		this.returnValues = returnValues;
	}
	
}
