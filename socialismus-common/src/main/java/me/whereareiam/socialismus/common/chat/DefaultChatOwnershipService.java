package me.whereareiam.socialismus.common.chat;

import com.google.inject.Singleton;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.service.chat.ChatOwnership;
import me.whereareiam.socialismus.service.chat.ChatOwnershipService;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Singleton
public class DefaultChatOwnershipService implements ChatOwnershipService {
	private final List<ChatOwnership> ownerships = new CopyOnWriteArrayList<>();

	@Override
	public boolean handles(@NotNull SocialismusPlayer player) {
		for (ChatOwnership ownership : ownerships) {
			if (!ownership.handles(player)) return false;
		}

		return true;
	}

	@Override
	public void register(@NotNull ChatOwnership ownership) {
		ownerships.add(ownership);
	}

	@Override
	public void unregister(@NotNull ChatOwnership ownership) {
		ownerships.remove(ownership);
	}
}
