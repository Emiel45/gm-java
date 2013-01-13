package gmod;



import com.google.common.eventbus.EventBus;

public class Hook {
	
	private static final EventBus eventBus = new EventBus();
	
	public static void register(Object o) {
		eventBus.register(o);
	}
	
	/*public static void init() {
		Lua.getglobal("hook");
		Lua.getfield(-1, "Add");
		
		for(Type hookType : Type.values()) {
			if(hookType.getEventClass() == null) continue;
			
			Lua.pushvalue(-1);
			Lua.pushstring(hookType.getName());
			Lua.pushstring("java.hooks." + hookType.toString());
			Lua.pushobject(hookType);
			Lua.pushclosure(handler, 1);
			System.out.println("Hooking: " + hookType);
			Lua.call(3, 0);
		}
	}*/

	private static class Handler implements Lua.Function {

		@Override
		public int invoke() throws Exception {
			/*try {

				Class<? extends Event> eventClass = hookType.getEventClass();
				if(eventClass != null) {
					Event event = eventClass.newInstance();
					event.parse();
					
					eventBus.post(event);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return 0;*/
			return 0;
		}
		
	}
	
}
