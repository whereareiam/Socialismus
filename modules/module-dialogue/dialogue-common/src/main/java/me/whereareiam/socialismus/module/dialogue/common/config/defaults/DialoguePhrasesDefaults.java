package me.whereareiam.socialismus.module.dialogue.common.config.defaults;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.whereareiam.socialismus.model.phrase.Phrases;

/**
 * The phrases Dialogue ships: under {@code sender}, what a sender is told when {@code messaging.requires} refuses
 * them, and under {@code recipient}, when {@code messaging.reaches} refuses the player they write to.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DialoguePhrasesDefaults {
	/**
	 * The default phrases.
	 */
	public static final Phrases PHRASES = Phrases.builder()
			.section("dialogue", dialogue -> dialogue
					.section("denied", denied -> denied
							.section("sender", sender -> sender
									.text("permission.missing", "{prefix}<white>You may not send private messages.")
									.text("world.outside", "{prefix}<white>Private messages only work in <gray>{worlds}</gray>.")
									.text("world.inside", "{prefix}<white>Private messages do not work in <gray>{world}</gray>.")
									.text("server.outside", "{prefix}<white>Private messages only work on <gray>{servers}</gray>.")
									.text("server.inside", "{prefix}<white>Private messages do not work on <gray>{server}</gray>."))
							.section("recipient", recipient -> recipient
									.text("permission.missing", "{prefix}<gray>{recipientName}</gray> <white>may not receive private messages.")
									.text("world.outside", "{prefix}<gray>{recipientName}</gray> <white>is in <gray>{world}</gray>, which private messages do not reach.")
									.text("world.inside", "{prefix}<gray>{recipientName}</gray> <white>is in <gray>{world}</gray>, which private messages do not reach.")
									.text("server.outside", "{prefix}<gray>{recipientName}</gray> <white>is on <gray>{server}</gray>, which private messages do not reach.")
									.text("server.inside", "{prefix}<gray>{recipientName}</gray> <white>is on <gray>{server}</gray>, which private messages do not reach."))))
			.build();
}
