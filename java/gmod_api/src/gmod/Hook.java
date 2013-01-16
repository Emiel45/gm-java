package gmod;

import java.lang.reflect.Method;
import java.util.Map;

import com.google.common.collect.Maps;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import static gmod.Lua.Table._G;

public class Hook {

	private static final EventBus eventBus = new EventBus();
	private static final Map<String, Handler> eventHandlers = Maps.newHashMap();

	public static void register(Object o) {
		eventBus.register(o);

		for (Method m : o.getClass().getMethods()) {
			if (!m.isAnnotationPresent(Subscribe.class))
				continue;

			if (m.getParameterTypes().length != 1)
				continue;

			Class<?> parmClass = m.getParameterTypes()[0];
			if (!Event.class.isAssignableFrom(parmClass))
				continue;

			Class<? extends Event> eventClass = parmClass.asSubclass(Event.class);
			if (!eventClass.isAnnotationPresent(Event.Info.class))
				continue;

			Event.Info eventInfo = eventClass.getAnnotation(Event.Info.class);
			if (!eventHandlers.containsKey(eventInfo.name())) {
				Handler eventHandler = new Handler(eventClass);
				eventHandlers.put(eventInfo.name(), eventHandler);
				_G.getFieldTable("hook").invokeVoid("Add", eventInfo.name(), "java." + eventClass.getName(), eventHandler);
			}
		}
	}

	public static void unregister(Object o) {
		eventBus.unregister(o);
	}

	private static class Handler implements Lua.Function {

		private Class<? extends Event> eventClass;
		
		public Handler(Class<? extends Event> eventClass) {
			this.eventClass = eventClass;
		}
		
		@Override
		public int invoke(int nargs, int nresults) {
			Event event;
			try {
				event = eventClass.newInstance();
				eventBus.post(event);
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}

			return 0;
		}

	}

}
