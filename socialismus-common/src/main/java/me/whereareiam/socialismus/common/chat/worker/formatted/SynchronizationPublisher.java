package me.whereareiam.socialismus.common.chat.worker.formatted;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.Constants;
import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.model.Worker;
import me.whereareiam.socialismus.model.chat.ChatSync;
import me.whereareiam.socialismus.model.chat.message.FormattedChatMessage;
import me.whereareiam.socialismus.registry.WorkerProcessor;
import me.whereareiam.socialismus.service.sync.ChatSyncBus;

@Singleton
public class SynchronizationPublisher {
	private final ChatSyncBus syncBus;

	// Configs
	private final Provider<ChatSync> chatSync;

	@Inject
	public SynchronizationPublisher(
			WorkerProcessor<FormattedChatMessage> workerProcessor,
			ChatSyncBus syncBus,
			Provider<ChatSync> chatSync
	) {
		this.syncBus = syncBus;
		this.chatSync = chatSync;

		workerProcessor.addWorker(new Worker<>(this::publish, 50, true, false));
	}

	private FormattedChatMessage publish(FormattedChatMessage msg) {
		if (!chatSync.get().isEnabled()) return msg;
		if (msg.getOrigin() != null && !msg.getOrigin().equals(Constants.Synchronization.IDENTIFIER)) return msg;
		if (determineRadius(msg) > 0) return msg;

		syncBus.publish(msg);
		Logger.debug("Synced formatted chat #%s from %s",
				msg.getId(), msg.getSender().getUsername());

		return msg;
	}

	private int determineRadius(FormattedChatMessage message) {
		if (message.getTrigger() != null && message.getTrigger().getRadius() != null)
			return message.getTrigger().getRadius();

		if (message.getChat() != null && message.getChat().getTriggers() != null) {
			return message.getChat().getTriggers().stream()
					.map(trigger -> trigger.getRadius() == null ? 0 : trigger.getRadius())
					.filter(radius -> radius > 0)
					.findFirst()
					.orElse(0);
		}

		return 0;
	}
}
