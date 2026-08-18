package me.whereareiam.socialismus.module.chirper.common.broadcast;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.keystone.Actor;
import me.whereareiam.socialismus.Serializer;
import me.whereareiam.socialismus.module.chirper.api.model.announcement.AnnouncementContent;
import me.whereareiam.socialismus.module.chirper.api.model.announcement.variant.*;
import me.whereareiam.socialismus.module.chirper.api.type.AnnouncementType;
import me.whereareiam.socialismus.module.chirper.common.util.BossBarUtil;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.title.Title;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class BroadcastSender {
	private final BossBarUtil bossBarUtil;

	public void sendContent(AnnouncementType type, AnnouncementContent content, Actor recipient) {
		Audience audience = recipient.getAudience();

		switch (type) {
			case MESSAGE -> audience.sendMessage(
					Serializer.serialize(recipient, String.join("\n", ((MessageAnnouncement) content).getMessages()))
			);
			case BOSSBAR -> bossBarUtil.createTemporaryBossBar(
					recipient,
					BossBar.bossBar(
							Serializer.serialize(recipient, ((BossBarAnnouncement) content).getMessage()),
							1.0f,
							((BossBarAnnouncement) content).getColor(),
							((BossBarAnnouncement) content).getOverlay()
					),
					((BossBarAnnouncement) content).getSettings().getDuration()
			);
			case TITLE -> {
				TitleAnnouncement title = (TitleAnnouncement) content;
				audience.showTitle(Title.title(
						Serializer.serialize(recipient, title.getTitle()),
						Serializer.serialize(recipient, title.getSubtitle()),
						Title.Times.times(Duration.of(title.getSettings().getFadeIn(), ChronoUnit.MILLIS),
								Duration.of(title.getSettings().getStay(), ChronoUnit.MILLIS),
								Duration.of(title.getSettings().getFadeOut(), ChronoUnit.MILLIS)
						)
				));
			}
			case ACTIONBAR -> audience.sendActionBar(
					Serializer.serialize(recipient, ((ActionbarAnnouncement) content).getMessage())
			);
			case SOUND -> {
				SoundAnnouncement sound = (SoundAnnouncement) content;
				audience.playSound(Sound.sound(
						Key.key(sound.getSound()),
						Sound.Source.MASTER,
						sound.getSettings().getVolume(),
						sound.getSettings().getPitch()
				));
			}
		}
	}
}
