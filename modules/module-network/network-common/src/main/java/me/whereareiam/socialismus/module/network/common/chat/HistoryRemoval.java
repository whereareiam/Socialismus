package me.whereareiam.socialismus.module.network.common.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Messages removed from the chat history on one node, for the others to remove too: one by its id, the latest
 * {@code amount}, or every message of a player.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class HistoryRemoval {
	private Kind kind;
	private int id;
	private int amount;
	private String username;

	public enum Kind {
		ID, AMOUNT, PLAYER
	}
}
