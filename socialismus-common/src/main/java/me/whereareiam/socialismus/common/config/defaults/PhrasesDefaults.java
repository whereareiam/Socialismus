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
					.text("chat.inside", "{prefix}<white>This does not work in <gray>{chat}</gray> chat.</white>"))
			.section("chat", chat -> chat
					.section("denied", denied -> denied
							.text("permission.missing", "{prefix}<white>You may not write in <gray>{chat}</gray> chat.</white>")
							.text("world.outside", "{prefix}<white><gray>{chat}</gray> chat only works in <gray>{worlds}</gray>.</white>")
							.text("world.inside", "{prefix}<white><gray>{chat}</gray> chat does not work in <gray>{world}</gray>.</white>")
							.text("server.outside", "{prefix}<white><gray>{chat}</gray> chat only works on <gray>{servers}</gray>.</white>")
							.text("server.inside", "{prefix}<white><gray>{chat}</gray> chat does not work on <gray>{server}</gray>.</white>")))
			.build();
}
