package me.whereareiam.socialismus.module.essentials.feature.dialogue.model.conversation;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Set;
import java.util.TreeSet;

@Getter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public class ConversationKey {
	private final String key;

	public static ConversationKey of(String player1, String player2) {
		// Create consistent key regardless of order
		Set<String> participants = new TreeSet<>();
		participants.add(player1.toLowerCase());
		participants.add(player2.toLowerCase());

		return new ConversationKey(String.join(":", participants));
	}

	public static ConversationKey of(Set<String> participants) {
		Set<String> sorted = new TreeSet<>();
		participants.forEach(p -> sorted.add(p.toLowerCase()));

		return new ConversationKey(String.join(":", sorted));
	}
}
