package me.whereareiam.socialismus.module.network.common.chat;

import me.whereareiam.socialismus.event.EventListener;
import me.whereareiam.socialismus.event.base.EventOrder;
import me.whereareiam.socialismus.event.base.SocialisticEvent;
import me.whereareiam.socialismus.event.chat.ChatBroadcastEvent;
import me.whereareiam.socialismus.event.chat.history.ChatHistoryRemoveByAmountEvent;
import me.whereareiam.socialismus.event.chat.history.ChatHistoryRemoveByIdEvent;
import me.whereareiam.socialismus.event.chat.history.ChatHistoryRemoveByPlayerEvent;
import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.model.chat.Chat;
import me.whereareiam.socialismus.model.chat.message.ChatMessage;
import me.whereareiam.socialismus.model.chat.message.FormattedChatMessage;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.module.network.api.NetworkAttributes;
import me.whereareiam.socialismus.module.network.api.model.Origin;
import me.whereareiam.socialismus.module.network.api.player.NetworkPlayer;
import me.whereareiam.socialismus.module.network.api.player.NetworkPlayers;
import me.whereareiam.socialismus.module.network.api.replication.ReplicatedChannel;
import me.whereareiam.socialismus.module.network.api.replication.ReplicationSystem;
import me.whereareiam.socialismus.module.network.common.config.NetworkSettings;
import me.whereareiam.socialismus.service.SerializationService;
import me.whereareiam.socialismus.service.chat.ChatCoordinationService;
import me.whereareiam.socialismus.service.chat.ChatHistoryService;
import me.whereareiam.socialismus.service.container.ChatContainerService;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * Carries chat between the nodes of the network: a message written on this node and broadcast here goes to the other
 * nodes, which deliver it to the players whose chat they handle and its {@code reaches} lets through, and so does a
 * removal from the chat history. A sender on another server is never {@code nearby}, so a chat that reaches only
 * nearby players stays on its node by itself. A message carries the attributes modules ask SocialismusNetwork to
 * carry. A message or removal that arrived from another node is never sent on.
 */
public final class ChatRelay implements EventListener {
	static final String CHAT_CHANNEL = "socialismus:chat";
	static final String HISTORY_CHANNEL = "socialismus:chat-history";

	private final ReplicatedChannel<ChatRecord> chat;
	private final ReplicatedChannel<HistoryRemoval> history;
	private final Supplier<NetworkSettings.Chat> settings;
	private final ChatCoordinationService coordination;
	private final ChatContainerService chats;
	private final ChatHistoryService chatHistory;
	private final NetworkPlayers players;
	private final CarriedAttributes attributes;
	private final SerializationService serialization;

	public ChatRelay(
			@NotNull ReplicationSystem replication,
			@NotNull Supplier<NetworkSettings.Chat> settings,
			@NotNull ChatCoordinationService coordination,
			@NotNull ChatContainerService chats,
			@NotNull ChatHistoryService chatHistory,
			@NotNull NetworkPlayers players,
			@NotNull CarriedAttributes attributes,
			@NotNull SerializationService serialization
	) {
		this.chat = replication.channel(CHAT_CHANNEL, ChatRecord.class);
		this.history = replication.channel(HISTORY_CHANNEL, HistoryRemoval.class);
		this.settings = settings;
		this.coordination = coordination;
		this.chats = chats;
		this.chatHistory = chatHistory;
		this.players = players;
		this.attributes = attributes;
		this.serialization = serialization;

		chat.subscribe(this::receive);
		history.subscribe(this::receive);
	}

	@SocialisticEvent(EventOrder.HIGHEST)
	public void onChatBroadcast(ChatBroadcastEvent event) {
		FormattedChatMessage message = event.getChatMessage();
		// A message that arrived from another node carries its origin and is never sent on.
		if (event.isCancelled() || message.getAttribute(NetworkAttributes.ORIGIN).isPresent()) return;
		if (!settings.get().isEnabled()) return;
		if (message.getChat() == null) return;

		SocialismusPlayer sender = message.getSender();
		chat.publish(new ChatRecord(
				message.getId(),
				sender.getUniqueId().toString(),
				sender.getUsername(),
				sender.getServer(),
				message.getChat().getId(),
				message.getContent(),
				message.getFormat(),
				attributes.write(message, serialization)
		));
	}

	@SocialisticEvent
	public void onRemoveById(ChatHistoryRemoveByIdEvent event) {
		publish(new HistoryRemoval(HistoryRemoval.Kind.ID, event.getId(), 0, null));
	}

	@SocialisticEvent
	public void onRemoveByAmount(ChatHistoryRemoveByAmountEvent event) {
		publish(new HistoryRemoval(HistoryRemoval.Kind.AMOUNT, 0, event.getAmount(), null));
	}

	@SocialisticEvent
	public void onRemoveByPlayer(ChatHistoryRemoveByPlayerEvent event) {
		publish(new HistoryRemoval(HistoryRemoval.Kind.PLAYER, 0, 0, event.getUsername()));
	}

	private void publish(HistoryRemoval removal) {
		if (settings.get().isEnabled() && settings.get().isClearHistory()) history.publish(removal);
	}

	private void receive(ChatRecord record, Origin origin) {
		if (!settings.get().isEnabled()) return;

		Chat chatOfRecord = chats.getChat(record.getChat()).orElse(null);
		if (chatOfRecord == null) {
			Logger.debug("Ignoring a message of chat %s from %s, which this node does not have", record.getChat(), origin);
			return;
		}

		UUID senderId = UUID.fromString(record.getSender());
		SocialismusPlayer sender = players.find(senderId)
				.map(SocialismusPlayer.class::cast)
				.orElseGet(() -> new NetworkPlayer(senderId, record.getSenderName(), origin.getNode(), record.getSenderServer()));

		if (settings.get().isPreserveFormat() && record.getFormat() != null) {
			FormattedChatMessage message = FormattedChatMessage.builder()
					.id(record.getId())
					.sender(sender)
					.recipients(new ArrayList<>())
					.content(record.getContent())
					.chat(chatOfRecord)
					.format(record.getFormat())
					.build();
			receive(message, record, origin);
			coordination.relay(message);
			return;
		}

		ChatMessage message = ChatMessage.builder()
				.id(record.getId())
				.sender(sender)
				.recipients(new ArrayList<>())
				.content(record.getContent())
				.chat(chatOfRecord)
				.build();
		receive(message, record, origin);
		coordination.relay(message);
	}

	private void receive(ChatMessage message, ChatRecord record, Origin origin) {
		message.setAttribute(NetworkAttributes.ORIGIN, origin);
		attributes.read(record.getAttributes() == null ? Map.of() : record.getAttributes(), message, serialization);
	}

	private void receive(HistoryRemoval removal, Origin origin) {
		if (!settings.get().isEnabled() || !settings.get().isClearHistory() || removal.getKind() == null) return;

		switch (removal.getKind()) {
			case ID -> chatHistory.removeMessage(removal.getId(), false);
			case AMOUNT -> chatHistory.removeMessages(removal.getAmount(), false);
			case PLAYER -> {
				if (removal.getUsername() != null) chatHistory.removeMessages(removal.getUsername(), false);
			}
		}
	}
}
