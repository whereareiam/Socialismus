package me.whereareiam.socialismus.common.chat.broadcast;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.model.chat.message.FormattedChatMessage;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class ChatBroadcastPolicy {

	public boolean allows(FormattedChatMessage msg) {
		// A relayed message has no vanilla message on this server to fall back on, so Socialismus always sends it.
		if (msg.isRelayed()) return true;

		return !msg.isVanillaSending();
	}
}
