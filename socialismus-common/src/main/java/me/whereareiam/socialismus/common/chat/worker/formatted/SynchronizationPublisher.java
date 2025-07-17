package me.whereareiam.socialismus.common.chat.worker.formatted;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.Constants;
import me.whereareiam.socialismus.api.Logger;
import me.whereareiam.socialismus.api.input.WorkerProcessor;
import me.whereareiam.socialismus.api.input.sync.ChatSyncBus;
import me.whereareiam.socialismus.api.model.Worker;
import me.whereareiam.socialismus.api.model.chat.ChatSettings;
import me.whereareiam.socialismus.api.model.chat.message.FormattedChatMessage;

@Singleton
public class SynchronizationPublisher {
	private final ChatSyncBus syncBus;

	// Configs
	private final Provider<ChatSettings> chatSettings;

	@Inject
	public SynchronizationPublisher(
			WorkerProcessor<FormattedChatMessage> workerProcessor,
			ChatSyncBus syncBus,
			Provider<ChatSettings> chatSettings
	) {
		this.syncBus = syncBus;
		this.chatSettings = chatSettings;

		workerProcessor.addWorker(new Worker<>(this::publish, 50, true, false));
	}

	private FormattedChatMessage publish(FormattedChatMessage msg) {
		if (!chatSettings.get().getSynchronization().isEnabled()) return msg;
		if (msg.getOrigin() != null && !msg.getOrigin().equals(Constants.IDENTIFIER)) return msg;

		syncBus.publish(msg);
		Logger.debug("Synced formatted chat #%s from %s",
				msg.getId(), msg.getSender().getUsername());

		return msg;
	}
}
