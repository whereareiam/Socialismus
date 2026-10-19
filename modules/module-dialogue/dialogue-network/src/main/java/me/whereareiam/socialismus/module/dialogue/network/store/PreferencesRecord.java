package me.whereareiam.socialismus.module.dialogue.network.store;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.whereareiam.socialismus.module.dialogue.api.model.DialoguePreferences;

/**
 * A player's preferences as they are replicated.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PreferencesRecord {
	private boolean messagesOff;
	private boolean spying;

	static PreferencesRecord of(DialoguePreferences preferences) {
		return new PreferencesRecord(preferences.isMessagesOff(), preferences.isSpying());
	}

	DialoguePreferences toPreferences() {
		return DialoguePreferences.builder().messagesOff(messagesOff).spying(spying).build();
	}
}
