package me.whereareiam.socialismus.common.chat.broadcast;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.model.chat.message.FormattedChatMessage;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class ChatBroadcastPolicy {

	public boolean allows(FormattedChatMessage msg) {
		// A message of a player this server does not host has no vanilla message here to fall back on, so Socialismus
		// always sends it.
		if (!msg.getSender().isHostedHere()) return true;

		return !msg.isVanillaSending();
	}
}
