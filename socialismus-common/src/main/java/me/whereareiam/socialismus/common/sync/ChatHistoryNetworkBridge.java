package me.whereareiam.socialismus.common.sync;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.api.Logger;
import me.whereareiam.socialismus.api.input.event.EventListener;
import me.whereareiam.socialismus.api.input.event.EventManager;
import me.whereareiam.socialismus.api.input.event.base.SocialisticEvent;
import me.whereareiam.socialismus.api.input.event.chat.history.ChatHistoryRemoveByAmountEvent;
import me.whereareiam.socialismus.api.input.event.chat.history.ChatHistoryRemoveByIdEvent;
import me.whereareiam.socialismus.api.input.event.chat.history.ChatHistoryRemoveByPlayerEvent;
import me.whereareiam.socialismus.api.input.event.chat.history.ChatHistoryRemoveEvent;
import me.whereareiam.socialismus.api.input.sync.ChatHistorySyncBus;
import me.whereareiam.socialismus.api.model.chat.ChatSettings;
import me.whereareiam.socialismus.api.output.SerializationService;
import me.whereareiam.socialismus.api.output.resource.sync.SyncService;
import me.whereareiam.socialismus.common.chat.ChatHistoryController;
import me.whereareiam.socialismus.shared.Constants;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class ChatHistoryNetworkBridge implements EventListener, ChatHistorySyncBus {
	private static final String CHANNEL = Constants.Channels.CHAT_HISTORY;

	private final SyncService sync;
	private final SerializationService serializationService;
	private final ChatHistoryController chatHistoryController;
	private final Provider<ChatSettings> chatSettings;
	private final EventManager eventManager;

	private final Map<Class<? extends ChatHistoryRemoveEvent>, Consumer<ChatHistoryRemoveEvent>> eventHandlers = new HashMap<>();

	public void initialize() {
		if (!chatSettings.get().getSynchronization().isEnabled()
				&& !chatSettings.get().getSynchronization().isClearHistory())
			return;

		eventManager.register(this);
		initializeEventHandlers();
		subscribe();
	}

	private void initializeEventHandlers() {
		eventHandlers.put(ChatHistoryRemoveByIdEvent.class, e -> chatHistoryController.removeMessage(((ChatHistoryRemoveByIdEvent) e).getId(), false));
		eventHandlers.put(ChatHistoryRemoveByAmountEvent.class, e -> chatHistoryController.removeMessages(((ChatHistoryRemoveByAmountEvent) e).getAmount(), false));
		eventHandlers.put(ChatHistoryRemoveByPlayerEvent.class, e -> chatHistoryController.removeMessages(((ChatHistoryRemoveByPlayerEvent) e).getUsername(), false));
	}

	@SocialisticEvent
	public void onChatHistoryRemove(ChatHistoryRemoveEvent event) {
		if (!chatSettings.get().getSynchronization().isEnabled())
			return;

		if (Constants.IDENTIFIER.equals(event.getOrigin()))
			publish(event);
	}

	@Override
	public void publish(ChatHistoryRemoveEvent event) {
		try {
			byte[] data = serializationService.serialize(event);
			sync.publish(CHANNEL, data);
			Logger.debug("Published chat history event to sync channel");
		} catch (Exception ex) {
			Logger.warn("Failed to sync chat history event: " + ex);
		}
	}

	@Override
	public void subscribe() {
		if (!chatSettings.get().getSynchronization().isEnabled())
			return;

		sync.subscribe(CHANNEL, (channel, payload) -> handleEvent(payload));
	}

	private void handleEvent(byte[] payload) {
		if (!chatSettings.get().getSynchronization().isClearHistory())
			return;

		try {
			ChatHistoryRemoveEvent event = serializationService.deserialize(payload, ChatHistoryRemoveEvent.class);

			if (Constants.IDENTIFIER.equals(event.getOrigin()))
				return;

			Logger.debug("Received chat history event from sync channel");
			eventHandlers.getOrDefault(event.getClass(), e -> {
			}).accept(event);
		} catch (Exception ex) {
			Logger.warn("Bad chat-history-sync packet: " + ex);
		}
	}
}
