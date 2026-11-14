package me.whereareiam.socialismus.module.mentionator.common.config.defaults;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.whereareiam.conditia.model.Node;
import me.whereareiam.conditia.model.Requirements;
import me.whereareiam.configura.Configura;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Writes the lists of the default files line by line, as an admin would.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class ConditionLines {
	static Requirements of(Node.Mapping... lines) {
		return Requirements.of(new Node.Sequence(List.of(lines)));
	}

	static Requirements of(List<Node.Mapping> lines) {
		return Requirements.of(new Node.Sequence(new ArrayList<Node>(lines)));
	}

	/**
	 * A line of one condition, such as {@code cooldown: 5m}.
	 */
	static Node.Mapping line(String condition, String value) {
		return Node.Mapping.of(condition, new Node.Scalar(value));
	}

	/**
	 * A line of one condition whose value is a block or a list, such as {@code sees: [target, sender]}.
	 */
	static Node.Mapping line(String condition, Node value) {
		return Node.Mapping.of(condition, value);
	}

	/**
	 * A list whose lines carry the text a refused sender is told, such as {@code cooldown: 5m} with
	 * {@code denied: "<ref:everyone-cooldown>"}, read as Configura reads the file. Building a line of two entries
	 * ourselves would call Conditia with the map types of Java 21, which the jar rewritten for Java 11 no longer
	 * names.
	 *
	 * @param configura reads the list
	 * @param lines     each line as its condition, its value and its text
	 */
	static Requirements denied(Configura configura, String[]... lines) {
		List<Map<String, String>> written = new ArrayList<>();
		for (String[] line : lines) {
			Map<String, String> entries = new LinkedHashMap<>();
			entries.put(line[0], line[1]);
			entries.put("denied", line[2]);
			written.add(entries);
		}

		return configura.mapper().convertValue(written, Requirements.class);
	}

	/**
	 * A line that holds when the line inside does not, such as {@code not: { dialogue:ignores: [target, sender] }}.
	 */
	static Node.Mapping not(Node.Mapping line) {
		return Node.Mapping.of("not", line);
	}

	/**
	 * A list of values, such as {@code [target, sender]}.
	 */
	static Node list(String... values) {
		return new Node.Sequence(Arrays.stream(values).<Node>map(Node.Scalar::new).toList());
	}
}
