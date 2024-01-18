package me.whereareiam.socialismus.core.listener.state;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.core.util.LoggerUtil;

@Singleton
public class ChatListenerState implements State {
	private static boolean isRequired = false;

	@Inject
	public ChatListenerState(LoggerUtil loggerUtil) {
		loggerUtil.trace("Initializing class: " + this);
	}

	public static boolean isRequired() {
		return isRequired;
	}

	public static void setRequired(boolean state) {
		isRequired = state;
	}
}
