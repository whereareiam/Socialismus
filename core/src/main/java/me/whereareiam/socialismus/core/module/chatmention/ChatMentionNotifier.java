package me.whereareiam.socialismus.core.module.chatmention;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.model.chatmention.ChatMentionNotificationFormat;
import me.whereareiam.socialismus.api.model.chatmention.mention.Mention;
import me.whereareiam.socialismus.api.type.ChatMentionNotificationType;
import me.whereareiam.socialismus.core.util.MessageUtil;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Singleton
public class ChatMentionNotifier {
	private final MessageUtil messageUtil;
	private final ChatMentionModule chatMentionModule;

	@Inject
	public ChatMentionNotifier(MessageUtil messageUtil, ChatMentionModule chatMentionModule) {
		this.messageUtil = messageUtil;
		this.chatMentionModule = chatMentionModule;
	}

	public void notifyPlayers(Mention mention) {
		Optional<ChatMentionNotificationFormat> format = chatMentionModule.getNotifications().stream().filter(
				f -> f.permission.isBlank() || f.permission.isEmpty() || mention.getSender().hasPermission(f.permission)
		).findFirst();

		if (format.isEmpty())
			return;

		for (ChatMentionNotificationType type : format.get().types) {
			switch (type) {
				case ACTION_BAR:
					mention.getMentionedPlayers().forEach(player -> messageUtil.sendActionBar(
							player,
							format.get().actionBar.message.replace("{mentionerName}", mention.getSender().getName())
					));
					break;
				case BOSS_BAR:
					mention.getMentionedPlayers().forEach(player -> messageUtil.sendBossBar(
							player,
							format.get().bossBar.message.replace("{mentionerName}", mention.getSender().getName()),
							format.get().bossBar.color,
							format.get().bossBar.style,
							format.get().bossBar.duration
					));
					break;
				case CHAT:
					mention.getMentionedPlayers().forEach(player -> messageUtil.sendMessage(
							player,
							format.get().chat.message.replace("{mentionerName}", mention.getSender().getName())
					));
					break;
				case SOUND:
					mention.getMentionedPlayers().forEach(player -> player.playSound(
							player,
							format.get().sound.sound,
							format.get().sound.volume,
							format.get().sound.pitch
					));
					break;
				case TITLE:
					mention.getMentionedPlayers().forEach(player -> messageUtil.sendTitle(
							player,
							format.get().title.title.replace("{mentionerName}", mention.getSender().getName()),
							format.get().title.subtitle.replace("{mentionerName}", mention.getSender().getName()),
							Duration.of(format.get().title.fadeIn, ChronoUnit.MILLIS),
							Duration.of(format.get().title.stay, ChronoUnit.MILLIS),
							Duration.of(format.get().title.fadeOut, ChronoUnit.MILLIS)
					));
					break;
			}
		}
	}
}
