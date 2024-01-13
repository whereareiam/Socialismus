package me.whereareiam.socialismus.module.announcer.announcement;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.model.announcement.Announcement;
import me.whereareiam.socialismus.model.announcer.Announcer;
import me.whereareiam.socialismus.module.announcer.AnnouncerRequirementValidator;
import me.whereareiam.socialismus.util.FormatterUtil;
import me.whereareiam.socialismus.util.LoggerUtil;
import me.whereareiam.socialismus.util.MessageUtil;
import me.whereareiam.socialismus.util.WorldPlayerUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.stream.Collectors;

@Singleton
public class AnnouncementBroadcaster {
    private final LoggerUtil loggerUtil;
    private final FormatterUtil formatterUtil;
    private final MessageUtil messageUtil;
    private final AnnouncerRequirementValidator announcerRequirementValidator;

    @Inject
    public AnnouncementBroadcaster(LoggerUtil loggerUtil, FormatterUtil formatterUtil, MessageUtil messageUtil,
                                   AnnouncerRequirementValidator announcerRequirementValidator) {
        this.loggerUtil = loggerUtil;
        this.formatterUtil = formatterUtil;
        this.messageUtil = messageUtil;
        this.announcerRequirementValidator = announcerRequirementValidator;
    }

    public void postAnnouncement(Announcer announcer, Announcement announcement) {
        loggerUtil.debug("Posting announcement: " + announcement.id + " (" + announcer.hashCode() + ")");
        Collection<? extends Player> onlinePlayers;

        if (announcement.requirements.worlds.isEmpty()) {
            onlinePlayers = Bukkit.getOnlinePlayers();
        } else {
            onlinePlayers = announcement.requirements.worlds.stream()
                    .flatMap(world -> WorldPlayerUtil.getPlayersInWorld(world).stream())
                    .collect(Collectors.toList());
        }

        onlinePlayers = announcerRequirementValidator.filterPlayers(announcement.requirements, onlinePlayers);

        String broadCastMessage = String.join("\n", announcement.message);
        Component broadcastMessage = formatterUtil.formatMessage(broadCastMessage);

        for (Player player : onlinePlayers) {
            messageUtil.sendMessage(player, messageUtil.replacePlaceholder(broadcastMessage, "{playerName}", player.getName()));
        }
    }
}
