package me.whereareiam.socialismus.common.chat.broadcast;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.Constants;
import me.whereareiam.socialismus.model.chat.message.FormattedChatMessage;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class ChatBroadcastPolicy {

	public boolean allows(FormattedChatMessage msg) {
		// 1) Cross‐server relay
		if (msg.getOrigin() != null
				&& !msg.getOrigin().equals(Constants.Synchronization.IDENTIFIER)) {
			return true;
		}

		// 2) Otherwise, only broadcast if it's non-vanilla.
		return !msg.isVanillaSending();
	}
}
