package me.whereareiam.socialismus.module.dialogue.network.store;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Whom one player ignores, as it is replicated, in the order they ignored them.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IgnoreRecord {
	private List<Entry> ignored = new ArrayList<>();

	/**
	 * One ignored player.
	 */
	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Entry {
		private String uniqueId;
		private String name;
	}
}
