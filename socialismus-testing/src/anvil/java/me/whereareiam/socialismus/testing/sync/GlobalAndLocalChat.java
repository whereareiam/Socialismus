package me.whereareiam.socialismus.testing.sync;

import me.whereareiam.socialismus.testing.environment.ServerFiles;

import java.util.function.Consumer;

import static me.whereareiam.socialismus.testing.environment.ServerFiles.chat;

/**
 * A message starting with {@code !} goes to global chat, which reaches every server of the network; every other one
 * to local chat, whose radius keeps it on its server.
 */
final class GlobalAndLocalChat implements Consumer<ServerFiles> {
	@Override
	public void accept(ServerFiles files) {
		files.chats(
				chat("local", 0, "", 64, "[L] {playerName}: {message}"),
				chat("global", 1, "!", 0, "[G] {playerName}: {message}")
		);
	}
}
