package me.whereareiam.socialismus.module.dialogue.common.store;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.module.dialogue.api.ConversationStore;
import me.whereareiam.socialismus.module.dialogue.api.model.conversation.Conversation;
import me.whereareiam.socialismus.module.dialogue.api.model.conversation.ConversationKey;
import org.jetbrains.annotations.NotNull;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Keeps conversations in the server's memory, so they last until it stops and stay on the server they were held on.
 */
@Singleton
public class InMemoryConversationStore implements ConversationStore {
	private static final Comparator<Conversation> MOST_RECENT_FIRST = Comparator.comparing(
			Conversation::getLastActivity,
			Comparator.nullsFirst(Comparator.<Instant>naturalOrder())
	).reversed();

	private final Map<ConversationKey, Entry> conversations = new ConcurrentHashMap<>();
	private final Clock clock;

	@Inject
	public InMemoryConversationStore() {
		this(Clock.systemUTC());
	}

	InMemoryConversationStore(@NotNull Clock clock) {
		this.clock = clock;
	}

	@Override
	public @NotNull Optional<Conversation> find(@NotNull ConversationKey key) {
		Entry entry = conversations.get(key);
		if (entry == null) return Optional.empty();
		if (entry.expired(clock.instant())) {
			conversations.remove(key, entry);
			return Optional.empty();
		}

		return Optional.of(entry.conversation());
	}

	@Override
	public @NotNull List<Conversation> findByParticipant(@NotNull String participant) {
		Instant now = clock.instant();
		conversations.values().removeIf(entry -> entry.expired(now));

		return conversations.values().stream()
				.map(Entry::conversation)
				.filter(conversation -> conversation.getParticipants().stream().anyMatch(participant::equalsIgnoreCase))
				.sorted(MOST_RECENT_FIRST)
				.toList();
	}

	@Override
	public void save(@NotNull Conversation conversation, @NotNull Duration ttl) {
		conversations.put(conversation.key(), new Entry(conversation, clock.instant().plus(ttl)));
	}

	@Override
	public void delete(@NotNull ConversationKey key) {
		conversations.remove(key);
	}

	private record Entry(Conversation conversation, Instant expiresAt) {
		boolean expired(Instant now) {
			return !now.isBefore(expiresAt);
		}
	}
}
