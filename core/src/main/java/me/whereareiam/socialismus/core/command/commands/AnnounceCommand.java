package me.whereareiam.socialismus.core.command.commands;

import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.*;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.core.command.base.CommandBase;
import me.whereareiam.socialismus.core.config.command.CommandsConfig;
import me.whereareiam.socialismus.core.config.message.MessagesConfig;
import me.whereareiam.socialismus.core.model.announcement.Announcement;
import me.whereareiam.socialismus.core.module.announcer.AnnouncerModule;
import me.whereareiam.socialismus.core.module.announcer.announcement.AnnouncementBroadcaster;
import me.whereareiam.socialismus.core.util.LoggerUtil;
import me.whereareiam.socialismus.core.util.MessageUtil;

@Singleton
@CommandAlias("%command.main")
public class AnnounceCommand extends CommandBase {
	private final LoggerUtil loggerUtil;
	private final MessageUtil messageUtil;
	private final CommandsConfig commands;
	private final MessagesConfig messages;

	private final AnnouncerModule announcerModule;
	private final AnnouncementBroadcaster announcementBroadcaster;

	@Inject
	public AnnounceCommand(LoggerUtil loggerUtil, MessageUtil messageUtil, CommandsConfig commands,
						   MessagesConfig messages, AnnouncerModule announcerModule,
						   AnnouncementBroadcaster announcementBroadcaster) {
		this.loggerUtil = loggerUtil;
		this.messageUtil = messageUtil;
		this.commands = commands;
		this.messages = messages;
		this.announcerModule = announcerModule;
		this.announcementBroadcaster = announcementBroadcaster;
	}

	@Subcommand("%command.announce")
	@CommandCompletion("@announcements")
	@CommandPermission("%permission.announce")
	@Description("%description.announce")
	@Syntax("%syntax.announce")
	public void onCommand(CommandIssuer issuer, String announcementId) {
		Announcement announcement = announcerModule.getAnnouncements().stream()
				.filter(announcement1 -> announcement1.id.equals(announcementId))
				.findFirst()
				.orElse(null);

		if (announcement == null) {
			messageUtil.sendMessage(issuer, messages.commands.announceCommand.noAnnouncement);
			return;
		}

		messageUtil.sendMessage(issuer, messages.commands.announceCommand.announce);
		announcementBroadcaster.postAnnouncement(announcement);
	}

	@Override
	public boolean isEnabled() {
		return commands.announceCommand.enabled;
	}

	@Override
	public void addReplacements() {
		commandHelper.addReplacement(commands.announceCommand.subCommand, "command.announce");
		commandHelper.addReplacement(commands.announceCommand.permission, "permission.announce");
		commandHelper.addReplacement(messages.commands.announceCommand.description, "description.announce");
		commandHelper.addReplacement(commands.announceCommand.syntax, "syntax.announce");
	}
}
