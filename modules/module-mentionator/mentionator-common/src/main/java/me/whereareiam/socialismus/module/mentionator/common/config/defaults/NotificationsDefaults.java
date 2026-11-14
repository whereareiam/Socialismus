package me.whereareiam.socialismus.module.mentionator.common.config.defaults;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.whereareiam.socialismus.module.mentionator.api.model.notification.Notification;
import me.whereareiam.socialismus.module.mentionator.api.model.notification.NotificationContent;
import me.whereareiam.socialismus.module.mentionator.api.model.notification.content.ActionbarContent;
import me.whereareiam.socialismus.module.mentionator.api.model.notification.content.BossbarContent;
import me.whereareiam.socialismus.module.mentionator.api.model.notification.content.ChatContent;
import me.whereareiam.socialismus.module.mentionator.api.model.notification.content.SoundContent;
import me.whereareiam.socialismus.module.mentionator.api.model.notification.content.TitleContent;
import me.whereareiam.socialismus.module.mentionator.common.config.dynamic.NotificationsConfig;
import net.kyori.adventure.bossbar.BossBar;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static me.whereareiam.socialismus.module.mentionator.common.config.defaults.ConditionLines.line;
import static me.whereareiam.socialismus.module.mentionator.common.config.defaults.ConditionLines.not;

/**
 * The notification files Mentionator writes when {@code notifications/} is empty: {@code pings}, the everyday ones, and
 * {@code alerts}, the loud ones.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NotificationsDefaults {
	/**
	 * Returns the default files by name.
	 *
	 * @param gameServer whether this is a game server, which knows the worlds players are in
	 * @return the files
	 */
	public static @NotNull Map<String, NotificationsConfig> files(boolean gameServer) {
		Map<String, NotificationsConfig> files = new LinkedHashMap<>();
		files.put("pings", pings());
		files.put("alerts", alerts(gameServer));

		return files;
	}

	static NotificationsConfig pings() {
		Notification ping = new Notification("ping", List.of(
				sound("minecraft:block.note_block.pling", 1.0f, 1.4f),
				ActionbarContent.builder().actionbar("<aqua>{sender}</aqua> <gray>tagged you in <white>{chat}</white> chat").build()
		));
		Notification quiet = new Notification("quiet", List.of(
				ActionbarContent.builder().actionbar("<gray>{sender} tagged you").build()
		));
		Notification whisper = new Notification("whisper", List.of(
				sound("minecraft:entity.experience_orb.pickup", 0.4f, 1.8f),
				ChatContent.builder().chat("<dark_gray>» <gray>{sender} tagged you: <white>{message}").build()
		));

		return new NotificationsConfig(new ArrayList<>(List.of(ping, quiet, whisper)));
	}

	static NotificationsConfig alerts(boolean gameServer) {
		TitleContent.TitleContentBuilder<?, ?> title = TitleContent.builder()
				.title("<gold>@{match}")
				.subtitle("<dark_gray>{sender}: {message}")
				.parameters(TitleContent.Parameters.builder().fadeIn(5).stay(40).fadeOut(10).build());
		// No title for players fighting in the arena; a proxy knows no worlds.
		if (gameServer) title.requires(ConditionLines.of(not(line("world", "pvp_arena"))));

		Notification alert = new Notification("alert", List.of(
				sound("minecraft:block.note_block.pling", 1.0f, 1.4f),
				title.build()
		));
		Notification everyone = new Notification("everyone", List.of(
				sound("minecraft:block.bell.use", 1.0f, 1.0f),
				BossbarContent.builder()
						.bossbar("<red>{sender}</red> <white>tagged everyone: <gray>{message}")
						.parameters(BossbarContent.Parameters.builder()
								.color(BossBar.Color.RED)
								.overlay(BossBar.Overlay.PROGRESS)
								.duration(Duration.ofSeconds(8))
								.build())
						.build()
		));

		return new NotificationsConfig(new ArrayList<>(List.of(alert, everyone)));
	}

	private static NotificationContent sound(String key, float volume, float pitch) {
		return SoundContent.builder()
				.sound(key)
				.parameters(SoundContent.Parameters.builder().volume(volume).pitch(pitch).build())
				.build();
	}
}
