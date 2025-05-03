package me.whereareiam.socialismus.core.integration.discordsrv;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.entities.MessageEmbed;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.util.WebhookUtil;
import me.whereareiam.socialismus.api.event.chat.AfterChatSendMessageEvent;
import me.whereareiam.socialismus.core.config.setting.SettingsConfig;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.time.Instant;
import java.time.temporal.TemporalAccessor;

@Singleton
public class DiscordChatHook implements Listener {
	private final SettingsConfig settings;

	@Inject
	public DiscordChatHook(
			SettingsConfig settings
	) {
		this.settings = settings;
	}

	@EventHandler
	public void onAfterChatSend(AfterChatSendMessageEvent event) {
		if (event.getChatMessage().getChat().requirements.recipient.radius > 0 && !settings.modules.chats.announceLocalChats)
			return;

		String content = PlainTextComponentSerializer.plainText().serialize(event.getChatMessage().getContent());
		Player player = event.getChatMessage().getSender();
		content = stripFormat(content, player.getName());

		Object object = DiscordSRV.config().getMap("Channels").get("socialismus");
		if (!(object instanceof String channel))
			return;

		TextChannel discordChannel = DiscordSRV.getPlugin()
				.getJda()
				.getTextChannelById(channel);

		WebhookUtil.deliverMessage(discordChannel, player, content);
	}

	private static String stripFormat(String raw, String playerName) {
		int idx = raw.indexOf(playerName);
		if (idx == -1)
			return raw;

		int start = idx + playerName.length();
		while (start < raw.length()) {
			char c = raw.charAt(start);
			if (c == ':' || c == '>' || c == '-' || Character.isWhitespace(c))
				start++;
			else break;
		}
		return raw.substring(start);
	}

}
