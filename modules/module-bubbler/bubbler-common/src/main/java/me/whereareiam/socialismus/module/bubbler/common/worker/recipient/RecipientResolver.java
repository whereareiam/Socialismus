package me.whereareiam.socialismus.module.bubbler.common.worker.recipient;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.model.Worker;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.BubbleMessage;
import me.whereareiam.socialismus.registry.PlayerRegistry;
import me.whereareiam.socialismus.registry.WorkerProcessor;

import java.util.stream.Collectors;

@Singleton
public class RecipientResolver {
    private final PlayerRegistry playerRegistry;

    @Inject
    public RecipientResolver(WorkerProcessor<BubbleMessage> workerProcessor, PlayerRegistry playerRegistry) {
        this.playerRegistry = playerRegistry;
        workerProcessor.addWorker(new Worker<>(this::resolveRecipients, 0, true, false));
    }

    private BubbleMessage resolveRecipients(BubbleMessage bubbleMessage) {
        if (!bubbleMessage.getRecipients().isEmpty()) return bubbleMessage;

        bubbleMessage.setRecipients(playerRegistry.getPlayers().stream()
                .filter(p -> !p.equals(bubbleMessage.getSender()))
                .filter(p -> p.getLocation().equals(bubbleMessage.getSender().getLocation()))
                .collect(Collectors.toSet()));

        return bubbleMessage;
    }
}
