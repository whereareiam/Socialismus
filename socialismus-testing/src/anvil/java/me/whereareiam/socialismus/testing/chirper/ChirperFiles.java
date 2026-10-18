package me.whereareiam.socialismus.testing.chirper;

import me.whereareiam.socialismus.model.requirement.RequirementGroup;
import me.whereareiam.socialismus.testing.environment.ServerFiles;
import me.whereareiam.socialismus.type.requirement.RequirementOperatorType;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Chirper's announcers and announcements, written as plain maps: Chirper's models hold Adventure's boss bar types,
 * which the journeys' classpath leaves out.
 *
 * <pre>{@code
 * ChirperFiles.announcers(files, ChirperFiles.announcer(2, "news"));
 * ChirperFiles.announcements(files, ChirperFiles.announcement("news", ChirperFiles.everyone(),
 *         ChirperFiles.message("Server news")));
 * }</pre>
 */
final class ChirperFiles {
	/** Chirper reads every file of these directories; naming the file {@code default} keeps its examples out. */
	private static final String ANNOUNCERS = "modules/Chirper/announcers/default.yml";
	private static final String ANNOUNCEMENTS = "modules/Chirper/announcements/default.yml";

	private ChirperFiles() {
	}

	/**
	 * Replaces Chirper's announcers with the given ones.
	 */
	@SafeVarargs
	static void announcers(@NotNull ServerFiles files, @NotNull Map<String, Object>... announcers) {
		files.put(ANNOUNCERS, Map.of("announcers", List.of(announcers)));
	}

	/**
	 * Replaces Chirper's announcements with the given ones.
	 */
	@SafeVarargs
	static void announcements(@NotNull ServerFiles files, @NotNull Map<String, Object>... announcements) {
		files.put(ANNOUNCEMENTS, Map.of("announcements", List.of(announcements)));
	}

	/**
	 * Describes an announcer that broadcasts the given announcements one after another, in the order of their
	 * identifiers, from the moment Chirper starts.
	 *
	 * @param interval      seconds between two announcements
	 * @param announcements the identifiers of its announcements
	 */
	@NotNull
	static Map<String, Object> announcer(int interval, @NotNull String... announcements) {
		return Map.of(
				"enabled", true,
				"announcements", List.of(announcements),
				"settings", Map.of("order", "ASCENDING", "minPlayers", 0, "interval", interval, "delay", 0)
		);
	}

	/**
	 * Describes an announcer that never runs, for journeys that only send announcements by command; Chirper falls
	 * back to its examples when it has no announcer at all.
	 */
	@NotNull
	static Map<String, Object> disabledAnnouncer(@NotNull String... announcements) {
		Map<String, Object> announcer = new LinkedHashMap<>(announcer(3600, announcements));
		announcer.put("enabled", false);

		return announcer;
	}

	/**
	 * Describes an announcement sent at once to every player who meets its requirements.
	 *
	 * @param id           the announcement's identifier
	 * @param requirements what a recipient meets
	 * @param contents     its contents, at most one of each kind
	 */
	@SafeVarargs
	@NotNull
	static Map<String, Object> announcement(
			@NotNull String id,
			@NotNull RequirementGroup requirements,
			@NotNull Map.Entry<String, Map<String, Object>>... contents
	) {
		Map<String, Object> byType = new LinkedHashMap<>();
		for (Map.Entry<String, Map<String, Object>> content : contents)
			byType.put(content.getKey(), content.getValue());

		return Map.of(
				"enabled", true,
				"id", id,
				"settings", Map.of("delay", 0, "repeat", true),
				"contents", byType,
				"requirements", requirements
		);
	}

	/**
	 * Returns requirements every player meets.
	 */
	@NotNull
	static RequirementGroup everyone() {
		return RequirementGroup.builder()
				.operator(RequirementOperatorType.AND)
				.groups(Map.of())
				.build();
	}

	/**
	 * Returns chat content of the given lines.
	 */
	@NotNull
	static Map.Entry<String, Map<String, Object>> message(@NotNull String... lines) {
		return content("MESSAGE", Map.of("messages", List.of(lines)));
	}

	/**
	 * Returns a title with a subtitle, its times in milliseconds.
	 */
	@NotNull
	static Map.Entry<String, Map<String, Object>> title(
			@NotNull String title,
			@NotNull String subtitle,
			int fadeIn,
			int stay,
			int fadeOut
	) {
		return content("TITLE", Map.of(
				"title", title,
				"subtitle", subtitle,
				"settings", Map.of("fadeIn", fadeIn, "stay", stay, "fadeOut", fadeOut)
		));
	}

	/**
	 * Returns an action bar.
	 */
	@NotNull
	static Map.Entry<String, Map<String, Object>> actionBar(@NotNull String message) {
		return content("ACTIONBAR", Map.of("message", message));
	}

	/**
	 * Returns a boss bar that empties over the given seconds and then leaves the screen.
	 *
	 * @param color   Adventure's name of its color, such as {@code RED}
	 * @param overlay Adventure's name of its overlay, such as {@code PROGRESS}
	 */
	@NotNull
	static Map.Entry<String, Map<String, Object>> bossBar(
			@NotNull String message,
			@NotNull String color,
			@NotNull String overlay,
			int duration
	) {
		return content("BOSSBAR", Map.of(
				"message", message,
				"color", color,
				"overlay", overlay,
				"settings", Map.of("breakable", true, "duration", duration)
		));
	}

	/**
	 * Returns a sound.
	 *
	 * @param sound the sound's key, such as {@code entity.player.levelup}
	 */
	@NotNull
	static Map.Entry<String, Map<String, Object>> sound(@NotNull String sound, float volume, float pitch) {
		return content("SOUND", Map.of("sound", sound, "settings", Map.of("volume", volume, "pitch", pitch)));
	}

	private static Map.Entry<String, Map<String, Object>> content(String type, Map<String, Object> fields) {
		Map<String, Object> content = new LinkedHashMap<>(fields);
		content.put("requirements", everyone());

		return Map.entry(type, content);
	}
}
