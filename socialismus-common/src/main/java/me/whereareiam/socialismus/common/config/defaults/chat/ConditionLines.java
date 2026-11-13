package me.whereareiam.socialismus.common.config.defaults.chat;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.whereareiam.conditia.model.Node;
import me.whereareiam.conditia.model.Requirements;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.SequencedMap;

/**
 * Writes the {@code requires} and {@code reaches} lists of the default chats line by line, as an admin would.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class ConditionLines {
	static Requirements of(Node.Mapping... lines) {
		return Requirements.of(new Node.Sequence(List.of(lines)));
	}

	/**
	 * A line of one condition, such as {@code nearby: 200}.
	 */
	static Node.Mapping line(String condition, String value) {
		return Node.Mapping.of(condition, new Node.Scalar(value));
	}

	/**
	 * A line of one condition whose value is a block or a list, such as {@code same: { placeholder: "%x%" }}.
	 */
	static Node.Mapping line(String condition, Node value) {
		return Node.Mapping.of(condition, value);
	}

	/**
	 * The line with the text a refused player is told, such as {@code denied: "<ref:shout-unheard>"}.
	 */
	static Node.Mapping denied(Node.Mapping line, String text) {
		SequencedMap<String, Node> entries = new LinkedHashMap<>(line.entries());
		entries.put("denied", new Node.Scalar(text));

		return new Node.Mapping(entries);
	}

	/**
	 * A line that holds when the line inside does not, such as {@code not: { world: world_nether }}.
	 */
	static Node.Mapping not(Node.Mapping line) {
		return Node.Mapping.of("not", line);
	}

	/**
	 * A block of settings, given as keys each followed by its value, such as {@code { text: "%x%", is: "" }}.
	 */
	static Node.Mapping block(String... settings) {
		SequencedMap<String, Node> entries = new LinkedHashMap<>();
		for (int index = 0; index + 1 < settings.length; index += 2)
			entries.put(settings[index], new Node.Scalar(settings[index + 1]));

		return new Node.Mapping(entries);
	}

	/**
	 * A list of values, such as {@code [de, de_at]}.
	 */
	static Node list(String... values) {
		return new Node.Sequence(Arrays.stream(values).<Node>map(Node.Scalar::new).toList());
	}
}
