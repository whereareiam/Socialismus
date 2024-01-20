package me.whereareiam.socialismus.core.module.chatmention.mention;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.model.chatmention.mention.Mention;
import me.whereareiam.socialismus.core.config.module.chatmention.ChatMentionConfig;
import me.whereareiam.socialismus.core.util.FormatterUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;

@Singleton
public class MentionFactory {
	private final FormatterUtil formatterUtil;
	private final ChatMentionConfig chatMentionConfig;

	@Inject
	public MentionFactory(FormatterUtil formatterUtil, ChatMentionConfig chatMentionConfig) {
		this.formatterUtil = formatterUtil;
		this.chatMentionConfig = chatMentionConfig;
	}

	public Mention createMention(Player sender, Collection<? extends Player> recipients, Component content) {
		List<String> plainContent = List.of(PlainTextComponentSerializer.plainText().serialize(content).split(" "));

		String allFormat = formatterUtil.cleanMessage(chatMentionConfig.settings.allFormat);
		boolean allTag = plainContent.stream().allMatch(s -> s.equals(allFormat));
		String nearbyFormat = formatterUtil.cleanMessage(chatMentionConfig.settings.nearbyFormat);
		boolean nearbyTag = plainContent.stream().allMatch(s -> s.equals(nearbyFormat));

		recipients = recipients.stream()
				.filter(p -> plainContent.stream().anyMatch(s -> s.equals(p.getName())))
				.toList();

		return new Mention(content, allTag, nearbyTag, sender, recipients);
	}
}
