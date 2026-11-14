package me.whereareiam.socialismus.module.mentionator.common.mode;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.model.chat.message.ChatMessage;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.module.mentionator.api.MentionatorService;
import me.whereareiam.socialismus.module.mentionator.api.model.tag.Tag;
import me.whereareiam.socialismus.module.mentionator.api.type.Mode;
import me.whereareiam.socialismus.module.mentionator.common.MentionatorStores;
import me.whereareiam.socialismus.module.mentionator.common.condition.Catalog;
import me.whereareiam.socialismus.module.mentionator.common.condition.MentionatorConditions;
import me.whereareiam.socialismus.store.Store;
import me.whereareiam.socialismus.store.Stores;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Keeps players' modes in {@link MentionatorStores#MODES}, and tells which tags a player may use.
 */
@Singleton
public class DefaultMentionatorService implements MentionatorService {
	private final Store<UUID, Mode> modes;
	private final MentionatorConditions conditions;

	@Inject
	public DefaultMentionatorService(@NotNull Stores stores, @NotNull MentionatorConditions conditions) {
		this.modes = stores.of(MentionatorStores.MODES);
		this.conditions = conditions;
	}

	@Override
	public @NotNull Mode getMode(@NotNull UUID player) {
		return modes.get(player).orElse(Mode.ALL);
	}

	@Override
	public void setMode(@NotNull UUID player, @NotNull Mode mode) {
		if (mode == Mode.ALL) {
			modes.remove(player);
			return;
		}

		modes.put(player, mode);
	}

	@Override
	public @NotNull List<Tag> usableTags(@NotNull SocialismusPlayer player) {
		Catalog catalog = conditions.catalog();
		// Nothing written yet: a tag whose requires look at the message is checked against an empty one.
		ChatMessage message = ChatMessage.builder()
				.sender(player)
				.recipients(new ArrayList<>())
				.content(Component.empty())
				.build();

		return catalog.getTags().stream()
				.filter(tag -> catalog.uses(tag, player, message).passed())
				.toList();
	}
}
