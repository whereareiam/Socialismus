package me.whereareiam.socialismus.module.network.common.chat;

import me.whereareiam.socialismus.event.EventListener;
import me.whereareiam.socialismus.event.base.EventOrder;
import me.whereareiam.socialismus.event.base.SocialisticEvent;
import me.whereareiam.socialismus.event.chat.ChatBroadcastEvent;
import me.whereareiam.socialismus.event.chat.history.ChatHistoryRemoveEvent;
import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.model.chat.Chat;
import me.whereareiam.socialismus.model.chat.history.HistorySelection;
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
import me.whereareiam.socialismus.service.chat.ChatCoordinationService;
import me.whereareiam.socialismus.service.chat.ChatHistoryService;
import me.whereareiam.socialismus.service.container.ChatContainerService;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * Carries chat between the nodes of the network: a message written on this node and broadcast here goes to the other
 * nodes, which deliver it to the players whose chat they handle and its {@code reaches} lets through, and so does a
 * removal from the chat history. A sender on another server is never {@code nearby}, so a chat that reaches only
 * nearby players stays on its node by itself. A message or removal that arrived from another node is never sent on:
 * a message is marked relayed, and a removal is recognised by the very selection this relay applied.
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
	/**
	 * The removals this relay applied for another node, by identity, so their events are not sent back.
	 */
	private final Set<HistorySelection> applied = Collections.synchronizedSet(Collections.newSetFromMap(new IdentityHashMap<>()));

	public ChatRelay(
			@NotNull ReplicationSystem replication,
			@NotNull Supplier<NetworkSettings.Chat> settings,
			@NotNull ChatCoordinationService coordination,
			@NotNull ChatContainerService chats,
			@NotNull ChatHistoryService chatHistory,
			@NotNull NetworkPlayers players
	) {
		this.chat = replication.channel(CHAT_CHANNEL, ChatRecord.class);
		this.history = replication.channel(HISTORY_CHANNEL, HistoryRemoval.class);
		this.settings = settings;
		this.coordination = coordination;
		this.chats = chats;
		this.chatHistory = chatHistory;
		this.players = players;

		chat.subscribe(this::receive);
		history.subscribe(this::receive);
	}

	@SocialisticEvent(EventOrder.HIGHEST)
	public void onChatBroadcast(ChatBroadcastEvent event) {
		FormattedChatMessage message = event.getChatMessage();
		if (event.isCancelled() || message.isRelayed() || !settings.get().isEnabled()) return;
		if (message.getChat() == null) return;

		SocialismusPlayer sender = message.getSender();
		chat.publish(new ChatRecord(
				message.getId(),
				sender.getUniqueId().toString(),
				sender.getUsername(),
				sender.getServer(),
				message.getChat().getId(),
				message.getContent(),
				message.getFormat()
		));
	}

	@SocialisticEvent
	public void onHistoryRemove(ChatHistoryRemoveEvent event) {
		if (applied.remove(event.getSelection())) return;
		if (!settings.get().isEnabled() || !settings.get().isClearHistory()) return;

		history.publish(switch (event.getSelection()) {
			case HistorySelection.Message message -> new HistoryRemoval(HistoryRemoval.Kind.ID, message.id(), 0, null);
			case HistorySelection.Oldest oldest -> new HistoryRemoval(HistoryRemoval.Kind.AMOUNT, 0, oldest.amount(), null);
			case HistorySelection.OfPlayer player -> new HistoryRemoval(HistoryRemoval.Kind.PLAYER, 0, 0, player.player().getUsername());
			case HistorySelection.ByName name -> new HistoryRemoval(HistoryRemoval.Kind.PLAYER, 0, 0, name.username());
		});
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
			message.setAttribute(NetworkAttributes.ORIGIN, origin);
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
		message.setAttribute(NetworkAttributes.ORIGIN, origin);
		coordination.relay(message);
	}

	private void receive(HistoryRemoval removal, Origin origin) {
		if (!settings.get().isEnabled() || !settings.get().isClearHistory() || removal.getKind() == null) return;

		HistorySelection selection = switch (removal.getKind()) {
			case ID -> HistorySelection.message(removal.getId());
			case AMOUNT -> HistorySelection.oldest(removal.getAmount());
			case PLAYER -> removal.getUsername() == null ? null : HistorySelection.byName(removal.getUsername());
		};
		if (selection == null) return;

		// Marked before the removal, since its event reaches onHistoryRemove on another thread, maybe before it returns.
		applied.add(selection);
		if (chatHistory.remove(selection) == 0) applied.remove(selection);
	}
}
