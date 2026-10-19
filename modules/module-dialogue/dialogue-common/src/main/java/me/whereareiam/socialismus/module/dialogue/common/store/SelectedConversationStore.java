package me.whereareiam.socialismus.module.dialogue.common.store;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.module.dialogue.api.ConversationStore;
import me.whereareiam.socialismus.module.dialogue.api.model.conversation.Conversation;
import me.whereareiam.socialismus.module.dialogue.api.model.conversation.ConversationKey;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

/**
 * The conversation store {@link DialogueStores} chose.
 */
@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class SelectedConversationStore implements ConversationStore {
	private final DialogueStores stores;

	@Override
	public @NotNull Optional<Conversation> find(@NotNull ConversationKey key) {
		return stores.conversations().find(key);
	}

	@Override
	public @NotNull List<Conversation> findByParticipant(@NotNull String participant) {
		return stores.conversations().findByParticipant(participant);
	}

	@Override
	public void save(@NotNull Conversation conversation, @NotNull Duration ttl) {
		stores.conversations().save(conversation, ttl);
	}

	@Override
	public void delete(@NotNull ConversationKey key) {
		stores.conversations().delete(key);
	}
}
