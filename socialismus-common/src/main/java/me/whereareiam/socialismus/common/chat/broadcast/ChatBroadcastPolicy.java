package me.whereareiam.socialismus.common.chat.broadcast;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.api.model.chat.message.FormattedChatMessage;
import me.whereareiam.socialismus.api.model.config.Settings;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class ChatBroadcastPolicy {
	private final Provider<Settings> settings;

	public boolean allows(FormattedChatMessage msg) {
		var sync = settings.get().getSynchronization();

		// 1) Cross‐server relay
		if (msg.getOrigin() != null
				&& !msg.getOrigin().equals(sync.getServer())) {
			return true;
		}

		// 2) Otherwise, only broadcast if it's non-vanilla.
		return !msg.isVanillaSending();
	}
}
