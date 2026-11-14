package me.whereareiam.socialismus.module.mentionator.command.executor;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.commandant.annotation.Definition;
import me.whereareiam.keystone.model.SerializerContent;
import me.whereareiam.socialismus.Serializer;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.module.mentionator.api.MentionatorService;
import me.whereareiam.socialismus.module.mentionator.api.model.config.MentionatorMessages;
import me.whereareiam.socialismus.module.mentionator.api.model.tag.Tag;
import me.whereareiam.socialismus.module.mentionator.api.type.Mode;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

/**
 * {@code /socialismus mentions}: shows a player which tags notify them and which tags they may use, or with a mode
 * chooses which tags notify them.
 */
@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class MentionsCommand {
	private final MentionatorService service;
	private final Provider<MentionatorMessages> messages;

	@Definition("mentions")
	@Command("socialismus mentions [mode]")
	public void command(@NotNull SocialismusPlayer player, @Argument("mode") @Nullable Mode mode) {
		MentionatorMessages.Mentions texts = messages.get().getMentions();
		if (mode == null) {
			player.sendMessage(Serializer.serialize(SerializerContent.builder()
					.receiver(player)
					.message(texts.getOverview())
					.placeholder("mode", named(texts, service.getMode(player.getUniqueId())))
					.placeholder("tags", tags(texts, service.usableTags(player)))
					.build()));
			return;
		}

		service.setMode(player.getUniqueId(), mode);
		player.sendMessage(Serializer.serialize(SerializerContent.builder()
				.receiver(player)
				.message(texts.getChanged())
				.placeholder("mode", named(texts, mode))
				.build()));
	}

	private static String named(MentionatorMessages.Mentions texts, Mode mode) {
		return texts.getModes() == null ? mode.name().toLowerCase() : texts.getModes().getOrDefault(mode, mode.name().toLowerCase());
	}

	private static String tags(MentionatorMessages.Mentions texts, List<Tag> tags) {
		if (tags.isEmpty()) return texts.getNoTags();

		return tags.stream()
				.map(tag -> texts.getTag()
						.replace("{tag}", tag.getId())
						.replace("{written}", String.join(", ", tag.getWritten())))
				.collect(Collectors.joining(texts.getSeparator()));
	}
}
