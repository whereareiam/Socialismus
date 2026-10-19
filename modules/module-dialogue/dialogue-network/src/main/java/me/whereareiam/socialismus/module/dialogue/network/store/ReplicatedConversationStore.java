package me.whereareiam.socialismus.module.dialogue.network.store;

import me.whereareiam.socialismus.module.dialogue.api.ConversationStore;
import me.whereareiam.socialismus.module.dialogue.api.model.conversation.Conversation;
import me.whereareiam.socialismus.module.dialogue.api.model.conversation.ConversationKey;
import me.whereareiam.socialismus.module.network.api.replication.ReplicatedCache;
import me.whereareiam.socialismus.module.network.api.replication.ReplicationSystem;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

/**
 * Conversations, shared by every node of the network, so {@code /reply} answers the same partner on whichever server
 * a player is. Only who talked and when travels; the messages stay on the node they were written on.
 */
public final class ReplicatedConversationStore implements ConversationStore {
	static final String CACHE = "dialogue:conversations";

	private final ReplicatedCache<ConversationRecord> records;

	public ReplicatedConversationStore(@NotNull ReplicationSystem replication) {
		this.records = replication.cache(CACHE, ConversationRecord.class);
	}

	@Override
	public @NotNull Optional<Conversation> find(@NotNull ConversationKey key) {
		return records.get(key.getKey()).map(ReplicatedConversationStore::conversation);
	}

	@Override
	public @NotNull List<Conversation> findByParticipant(@NotNull String participant) {
		return records.entries().values().stream()
				.filter(record -> record.getParticipants().stream().anyMatch(participant::equalsIgnoreCase))
				.sorted(Comparator.comparingLong(ConversationRecord::getLastActivity).reversed())
				.map(ReplicatedConversationStore::conversation)
				.toList();
	}

	@Override
	public void save(@NotNull Conversation conversation, @NotNull Duration ttl) {
		records.put(conversation.key().getKey(), new ConversationRecord(
				conversation.getId(),
				List.copyOf(conversation.getParticipants()),
				conversation.getLastSender(),
				conversation.getLastActivity() == null ? 0 : conversation.getLastActivity().toEpochMilli(),
				conversation.getMessageCount()
		), ttl);
	}

	@Override
	public void delete(@NotNull ConversationKey key) {
		records.remove(key.getKey());
	}

	private static Conversation conversation(ConversationRecord record) {
		return Conversation.builder()
				.id(record.getId())
				.participants(new LinkedHashSet<>(record.getParticipants()))
				.lastSender(record.getLastSender())
				.lastActivity(record.getLastActivity() == 0 ? null : Instant.ofEpochMilli(record.getLastActivity()))
				.messageCount(record.getMessageCount())
				.build();
	}
}
