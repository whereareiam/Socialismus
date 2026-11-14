package me.whereareiam.socialismus.common.chat.condition;

import com.google.inject.Inject;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.common.phrase.PhraseCheck;
import me.whereareiam.socialismus.lifecycle.RuntimeLifecycle;
import me.whereareiam.socialismus.lifecycle.StartupPhase;
import org.jetbrains.annotations.NotNull;

/**
 * Binds the conditions of the chats and the chat settings once the modules have registered their condition types,
 * so a chat's list may name one. A list that cannot be bound stops the startup with its path. Before, it tells the
 * admin which reasons no phrase words.
 */
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public final class ConditionLifecycle implements RuntimeLifecycle {
	private final DefaultChatConditions chats;
	private final ChatSettingsConditions settings;
	private final PhraseCheck phrases;

	@Override
	public @NotNull StartupPhase phase() {
		return StartupPhase.CONDITIONS;
	}

	@Override
	public void initialize() {
		phrases.run();
		settings.start();
		chats.start();
	}

	@Override
	public void shutdown() {
		chats.stop();
		settings.stop();
	}
}
