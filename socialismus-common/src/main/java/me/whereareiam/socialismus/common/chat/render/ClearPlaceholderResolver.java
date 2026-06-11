package me.whereareiam.socialismus.common.chat.render;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.Serializer;
import me.whereareiam.socialismus.model.chat.ChatMessages;
import me.whereareiam.socialismus.model.chat.ChatSettings;
import me.whereareiam.socialismus.model.chat.message.FormattedChatMessage;
import me.whereareiam.socialismus.model.chat.render.ChatRenderContext;
import me.whereareiam.socialismus.model.config.Commands;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.registry.base.Registry;
import me.whereareiam.socialismus.service.chat.render.ChatPlaceholderResolver;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;

import java.util.Map;

@Singleton
public class ClearPlaceholderResolver implements ChatPlaceholderResolver {
	private final Provider<ChatSettings> chatSettings;
	private final Provider<ChatMessages> chatMessages;
	private final Provider<Commands> commands;

	@Inject
	public ClearPlaceholderResolver(
			Provider<ChatSettings> chatSettings,
			Provider<ChatMessages> chatMessages,
			Provider<Commands> commands,
			Registry<ChatPlaceholderResolver> registry
	) {
		this.chatSettings = chatSettings;
		this.chatMessages = chatMessages;
		this.commands = commands;
		registry.register(this);
	}

	@Override
	public String key() {
		return "clear";
	}

	@Override
	public Component resolve(ChatRenderContext context) {
		if (context == null) return Component.empty();

		SocialismusPlayer recipient = context.getRecipient();
		if (recipient == null) return Component.empty();

		ChatSettings settings = chatSettings.get();
		ChatMessages messages = chatMessages.get();
		Commands commandConfig = commands.get();

		if (settings == null || settings.getHistory() == null || messages == null || messages.getClearFormat() == null) {
			return Component.empty();
		}

		SocialismusPlayer sender = context.getSender();
		String permission = settings.getHistory().getPermission();
		String bypass = settings.getHistory().getBypassPermission();

		if (permission == null || !recipient.hasPermission(permission)) {
			return Component.empty();
		}

		if (sender != null && bypass != null && sender.hasPermission(bypass)) {
			return Component.empty();
		}

		if (commandConfig == null
				|| commandConfig.getCommands() == null
				|| !commandConfig.getCommands().containsKey("clear")
				|| !commandConfig.getCommands().containsKey("main")) {
			return Component.empty();
		}

		String usage = commandConfig.getCommands().get("clear").getUsage();
		String command = commandConfig.getCommands().get("main").getAliases().get(0);
		String alias = commandConfig.getCommands().get("clear").getAliases().get(0);

		FormattedChatMessage message = context.getMessage();
		int messageId = message != null ? message.getId() : 0;

		String clickCommand = "/" + Serializer.renderTemplate(usage, Map.of(
				"command", command,
				"alias", alias
		))
				.replace("[context]", String.valueOf(messageId));

		SocialismusPlayer serializerPlayer = sender == null ? recipient : sender;
		return Serializer.serialize(serializerPlayer, messages.getClearFormat().getFormat())
				.clickEvent(ClickEvent.runCommand(clickCommand));
	}
}
