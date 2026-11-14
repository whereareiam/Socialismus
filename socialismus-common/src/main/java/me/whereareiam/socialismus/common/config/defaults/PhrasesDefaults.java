package me.whereareiam.socialismus.common.config.defaults;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.whereareiam.socialismus.model.phrase.Phrases;

/**
 * The phrases Socialismus ships: what a player is told for each reason a condition of Socialismus reports, and what
 * a chat tells for its own.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PhrasesDefaults {
	/**
	 * The default phrases.
	 */
	public static final Phrases PHRASES = Phrases.builder()
			.section("denied", denied -> denied
					.text("permission.missing", "{prefix}<white>You need the permission <gray>{permissions}</gray> for this.</white>")
					.text("permission.held", "{prefix}<white>This is not for players with <gray>{permissions}</gray>.</white>")
					.text("world.outside", "{prefix}<white>This only works in <gray>{worlds}</gray>.</white>")
					.text("world.inside", "{prefix}<white>This does not work in <gray>{world}</gray>.</white>")
					.text("server.outside", "{prefix}<white>This only works on <gray>{servers}</gray>.</white>")
					.text("server.inside", "{prefix}<white>This does not work on <gray>{server}</gray>.</white>")
					.text("chat.outside", "{prefix}<white>This only works in <gray>{chats}</gray> chat.</white>")
					.text("chat.inside", "{prefix}<white>This does not work in <gray>{chat}</gray> chat.</white>")
					.text("nearby.outside", "{prefix}<white>This only works within <gray>{blocks}</gray> blocks.</white>")
					.text("nearby.inside", "{prefix}<white>This does not work within <gray>{blocks}</gray> blocks.</white>")
					.text("same.outside", "{prefix}<white>This only works in the same <gray>{place}</gray>.</white>")
					.text("same.inside", "{prefix}<white>This does not work in the same <gray>{place}</gray>.</white>")
					.text("sees.hidden", "{prefix}<white>This player cannot see you.</white>")
					.text("language.outside", "{prefix}<white>This only works for <gray>{languages}</gray>.</white>")
					.text("language.inside", "{prefix}<white>This does not work for <gray>{language}</gray>.</white>")
					.text("cooldown.waiting", "{prefix}<white>Wait <gray>{remaining}</gray> before that again.</white>"))
			.section("chat", chat -> chat
					.section("denied", denied -> denied
							.text("permission.missing", "{prefix}<white>You may not write in <gray>{chat}</gray> chat.</white>")
							.text("world.outside", "{prefix}<white><gray>{chat}</gray> chat only works in <gray>{worlds}</gray>.</white>")
							.text("world.inside", "{prefix}<white><gray>{chat}</gray> chat does not work in <gray>{world}</gray>.</white>")
							.text("server.outside", "{prefix}<white><gray>{chat}</gray> chat only works on <gray>{servers}</gray>.</white>")
							.text("server.inside", "{prefix}<white><gray>{chat}</gray> chat does not work on <gray>{server}</gray>.</white>")
							.text("cooldown.waiting", "{prefix}<white>Wait <gray>{remaining}</gray> before writing in <gray>{chat}</gray> chat again.</white>")
							// What the sender of a message that reached nobody is told about the players it did not reach.
							.section("recipient", recipient -> recipient
									.text("permission.missing", "{prefix}<white>Nobody who may read <gray>{chat}</gray> chat received your message.</white>")
									.text("permission.held", "{prefix}<white>Nobody who may read <gray>{chat}</gray> chat received your message.</white>")
									.text("world.outside", "{prefix}<white>Nobody in <gray>{worlds}</gray> received your message.</white>")
									.text("world.inside", "{prefix}<white>Nobody outside <gray>{world}</gray> received your message.</white>")
									.text("server.outside", "{prefix}<white>Nobody on <gray>{servers}</gray> received your message.</white>")
									.text("server.inside", "{prefix}<white>Nobody outside <gray>{server}</gray> received your message.</white>")
									.text("nearby.outside", "{prefix}<white>No one within <gray>{blocks}</gray> blocks heard you.</white>")
									.text("same.outside", "{prefix}<white>No one in your <gray>{place}</gray> heard you.</white>")
									.text("sees.hidden", "{prefix}<white>Nobody who can see you received your message.</white>")
									.text("language.outside", "{prefix}<white>No one who speaks <gray>{languages}</gray> received your message.</white>")
									.text("placeholder.mismatch", "{prefix}<white>Nobody received your message.</white>")
									.text("placeholder.match", "{prefix}<white>Nobody received your message.</white>")))
					.text("nobody", "{prefix}<white>Nobody received your message.</white>")
					// Phrases of the showcase chats in chat/list/chats-default.yml; name your own the same way.
					.text("shout-unheard", "{prefix}<white>Nobody within <gray>{blocks}</gray> blocks heard your shout.</white>")
					.text("town-needed", "{prefix}<white>Join a town to write in <gray>{chat}</gray> chat.</white>"))
			.build();
}
