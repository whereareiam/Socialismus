package me.whereareiam.socialismus.module.mentionator.common.config.defaults;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.whereareiam.conditia.model.Node;
import me.whereareiam.conditia.model.Requirements;
import me.whereareiam.configura.Configura;
import me.whereareiam.socialismus.module.mentionator.api.model.tag.Tag;
import me.whereareiam.socialismus.module.mentionator.api.model.tag.TagFormat;
import me.whereareiam.socialismus.module.mentionator.api.model.tag.TagParameters;
import me.whereareiam.socialismus.module.mentionator.common.config.dynamic.TagsConfig;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static me.whereareiam.socialismus.module.mentionator.common.config.defaults.ConditionLines.denied;
import static me.whereareiam.socialismus.module.mentionator.common.config.defaults.ConditionLines.line;
import static me.whereareiam.socialismus.module.mentionator.common.config.defaults.ConditionLines.list;
import static me.whereareiam.socialismus.module.mentionator.common.config.defaults.ConditionLines.not;

/**
 * The tag files Mentionator writes when {@code tags/} is empty, as showcases: {@code players}, {@code groups} and
 * {@code roles}. Tags that need another plugin ship disabled.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TagsDefaults {
	/**
	 * Returns the default files by name.
	 *
	 * @param configura  reads the lists whose lines carry a text
	 * @param dialogue   whether Dialogue is installed, so tags skip players who ignore the sender
	 * @param gameServer whether this is a game server, which knows where players stand
	 * @return the files
	 */
	public static @NotNull Map<String, TagsConfig> files(@NotNull Configura configura, boolean dialogue, boolean gameServer) {
		Map<String, TagsConfig> files = new LinkedHashMap<>();
		files.put("players", players(dialogue));
		files.put("groups", groups(configura, dialogue, gameServer));
		files.put("roles", roles(dialogue));

		return files;
	}

	static TagsConfig players(boolean dialogue) {
		Tag player = Tag.builder()
				.id("player")
				.written(List.of("@{username}", "{username}"))
				.parameters(TagParameters.builder().minimumLength(3).build())
				.formats(List.of(
						new TagFormat("<aqua>@{match}</aqua>"),
						new TagFormat(
								"<click:suggest_command:'/msg {match} '><hover:show_text:'<gray>Message {match}'><aqua>@{match}</aqua></hover></click>",
								ConditionLines.of(line("permission", "socialismus.mentionator.format.clickable")),
								Requirements.none()
						),
						targetedFormat("<gold><bold>@{match}</bold></gold>")
				))
				.notify(List.of("ping"))
				.targets(targets(dialogue, line("sees", list("target", "sender"))))
				.build();

		// Essentials' nicknames, through PlaceholderAPI.
		Tag nickname = Tag.builder()
				.id("nickname")
				.enabled(false)
				.written(List.of("@%essentials_nickname_stripped%"))
				.parameters(TagParameters.builder().direct(true).refresh(Duration.ofMinutes(1)).build())
				.formats(List.of(
						new TagFormat("<aqua>@{match}</aqua>"),
						targetedFormat("<gold><bold>@{match}</bold></gold>")
				))
				.notify(List.of("ping"))
				.targets(targets(dialogue, line("sees", list("target", "sender"))))
				.build();

		return new TagsConfig(new ArrayList<>(List.of(player, nickname)));
	}

	static TagsConfig groups(Configura configura, boolean dialogue, boolean gameServer) {
		Tag everyone = Tag.builder()
				.id("everyone")
				.priority(10)
				.written(List.of("@everyone", "@all"))
				.parameters(TagParameters.builder().perMessage(1).build())
				.formats(List.of(new TagFormat("<red>@everyone</red>")))
				.notify(List.of("everyone"))
				.requires(denied(
						configura,
						new String[]{"permission", "socialismus.mentionator.tag.everyone", "<ref:everyone-denied>"},
						new String[]{"cooldown", "5m", "<ref:everyone-cooldown>"}
				))
				.targets(targets(dialogue))
				.build();

		Tag here = Tag.builder()
				.id("here")
				.priority(10)
				.written(List.of("@here"))
				.parameters(TagParameters.builder().perMessage(1).build())
				.formats(List.of(new TagFormat("<yellow>@here</yellow>")))
				.notify(List.of("quiet"))
				.requires(ConditionLines.of(line("permission", "socialismus.mentionator.tag.here")))
				.targets(targets(dialogue, line("mentionator:reached", list("target", "message"))))
				.build();

		Tag server = Tag.builder()
				.id("server")
				.priority(10)
				.written(List.of("@server"))
				.parameters(TagParameters.builder().perMessage(1).build())
				.formats(List.of(new TagFormat("<green>@server</green>")))
				.notify(List.of("ping"))
				.requires(ConditionLines.of(line("permission", "socialismus.mentionator.tag.server")))
				.targets(targets(dialogue, line("same", "server")))
				.build();

		List<Tag> tags = new ArrayList<>(List.of(everyone, here, server));
		if (!gameServer) return new TagsConfig(tags);

		Tag nearby = Tag.builder()
				.id("nearby")
				.priority(10)
				.written(List.of("@nearby", "@near"))
				.parameters(TagParameters.builder().perMessage(1).build())
				.formats(List.of(new TagFormat("<green>@nearby</green>")))
				.notify(List.of("ping"))
				.requires(ConditionLines.of(line("permission", "socialismus.mentionator.tag.nearby")))
				.targets(targets(dialogue, line("nearby", "100")))
				.build();
		tags.add(nearby);

		return new TagsConfig(tags);
	}

	static TagsConfig roles(boolean dialogue) {
		Tag staff = Tag.builder()
				.id("staff")
				.priority(5)
				.written(List.of("@staff"))
				.parameters(TagParameters.builder().perMessage(1).build())
				.formats(List.of(
						new TagFormat("<gold>@staff</gold>"),
						targetedFormat("<gold><bold>@staff</bold></gold>")
				))
				.notify(List.of("alert"))
				.targets(ConditionLines.of(line("permission", "socialismus.mentionator.staff")))
				.build();

		// LuckPerms' groups, through PlaceholderAPI: @admin tags every player whose primary group is admin.
		Tag rank = Tag.builder()
				.id("rank")
				.enabled(false)
				.written(List.of("@%luckperms_primary_group_name%"))
				.parameters(TagParameters.builder().perMessage(1).refresh(Duration.ofMinutes(1)).direct(false).build())
				.formats(List.of(
						new TagFormat("<green>@{match}</green>"),
						targetedFormat("<green><bold>@{match}</bold></green>")
				))
				.notify(List.of("ping"))
				.targets(targets(dialogue, line("sees", list("target", "sender"))))
				.build();

		return new TagsConfig(new ArrayList<>(List.of(staff, rank)));
	}

	private static TagFormat targetedFormat(String format) {
		return new TagFormat(format, ConditionLines.of(line("mentionator:targeted", list("recipient", "message"))));
	}

	// With Dialogue, a tag skips players who ignore the sender.
	private static Requirements targets(boolean dialogue, Node.Mapping... lines) {
		List<Node.Mapping> all = new ArrayList<>(List.of(lines));
		if (dialogue) all.add(not(line("dialogue:ignores", list("target", "sender"))));

		return all.isEmpty() ? Requirements.none() : ConditionLines.of(all);
	}
}
