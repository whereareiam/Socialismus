package me.whereareiam.socialismus.module.bubbler.api.model.bubble;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * Represents a group of bubble lines that are displayed together.
 * Groups are used to batch multiple lines of text that should appear
 * and disappear as a unit.
 */
@Getter
@SuperBuilder
@AllArgsConstructor
public class BubbleGroup {
	private List<BubbleLine> lines;

	/**
	 * Calculates the total display time for this group.
	 * The total is the sum of all individual line display times.
	 *
	 * @return the total display time in milliseconds
	 */
	public long calculateDisplayTime() {
		return lines.stream().mapToLong(BubbleLine::getDisplayTime).sum();
	}
}
