package me.whereareiam.socialismus.core.module.chatmention;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import me.whereareiam.socialismus.api.model.chatmention.ChatMentionFormat;
import me.whereareiam.socialismus.api.model.chatmention.ChatMentionNotificationFormat;
import me.whereareiam.socialismus.api.model.chatmention.notification.SoundNotification;
import me.whereareiam.socialismus.api.model.chatmention.notification.TitleNotification;
import me.whereareiam.socialismus.api.type.ChatMentionNotificationType;
import me.whereareiam.socialismus.core.config.module.chatmention.ChatMentionConfig;
import me.whereareiam.socialismus.core.config.setting.SettingsConfig;
import me.whereareiam.socialismus.core.listener.state.ChatListenerState;
import me.whereareiam.socialismus.core.module.Module;
import me.whereareiam.socialismus.core.util.LoggerUtil;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class ChatMentionModule implements Module {
	private final LoggerUtil loggerUtil;
	private final SettingsConfig settingsConfig;
	private final ChatMentionConfig chatMentionConfig;
	private final Path modulePath;

	private final List<ChatMentionFormat> formats = new ArrayList<>();
	private final List<ChatMentionNotificationFormat> notifications = new ArrayList<>();

	private boolean moduleStatus;

	@Inject
	public ChatMentionModule(LoggerUtil loggerUtil, SettingsConfig settingsConfig, ChatMentionConfig chatMentionConfig, @Named("modulePath") Path modulePath) {
		this.loggerUtil = loggerUtil;
		this.settingsConfig = settingsConfig;
		this.chatMentionConfig = chatMentionConfig;
		this.modulePath = modulePath;

		loggerUtil.trace("Initializing class: " + this);
	}

	private void registerChatMentions() {
		loggerUtil.debug("Reloading chatmentions.yml");
		chatMentionConfig.reload(modulePath.resolve("chatmentions.yml"));

		if (chatMentionConfig.formats.isEmpty()) {
			loggerUtil.debug("Creating an example format for chatmention, because format section is empty");
			createExampleFormat();
			chatMentionConfig.save(modulePath.resolve("chatmentions.yml"));
		}

		if (chatMentionConfig.notifications.isEmpty()) {
			loggerUtil.debug("Creating an example notification for chatmention, because notification section is empty");
			createExampleNotification();
			chatMentionConfig.save(modulePath.resolve("chatmentions.yml"));
		}

		chatMentionConfig.formats.stream().filter(chatMentionFormat -> chatMentionFormat.enabled).forEach(formats::add);
		chatMentionConfig.notifications.stream().filter(chatMentionNotificationFormat -> chatMentionNotificationFormat.enabled).forEach(notifications::add);
	}

	private void createExampleNotification() {
		ChatMentionNotificationFormat notificationFormat = new ChatMentionNotificationFormat();

		notificationFormat.enabled = true;
		notificationFormat.permission = "";
		notificationFormat.types = List.of(ChatMentionNotificationType.TITLE, ChatMentionNotificationType.SOUND);

		notificationFormat.title = new TitleNotification();
		notificationFormat.sound = new SoundNotification();

		chatMentionConfig.notifications.add(notificationFormat);
	}

	private void createExampleFormat() {
		ChatMentionFormat chatMentionFormat = new ChatMentionFormat();

		chatMentionFormat.enabled = true;
		chatMentionFormat.permission = "";
		chatMentionFormat.format = "<red>@{player}</red>";
		chatMentionFormat.hoverFormat = List.of("<gray>Player @{player} is mentioned in chat");

		chatMentionConfig.formats.add(chatMentionFormat);
	}

	public List<ChatMentionFormat> getFormats() {
		return formats;
	}

	public List<ChatMentionNotificationFormat> getNotifications() {
		return notifications;
	}

	@Override
	public void initialize() {
		ChatListenerState.setRequired(true);
		registerChatMentions();

		moduleStatus = true;
	}

	@Override
	public boolean isEnabled() {
		return moduleStatus == settingsConfig.modules.chatmention;
	}

	@Override
	public void reload() {
		loggerUtil.debug("Reloading chatmention module");
		formats.clear();
		notifications.clear();

		registerChatMentions();
	}
}
