package me.whereareiam.socialismus.common.chat.processor;

import com.google.inject.Singleton;
import lombok.Getter;
import me.whereareiam.socialismus.registry.WorkerProcessor;
import me.whereareiam.socialismus.model.Worker;
import me.whereareiam.socialismus.model.chat.message.ChatMessage;

import java.util.Comparator;
import java.util.LinkedList;

@Getter
@Singleton
public class ChatMessageProcessor implements WorkerProcessor<ChatMessage> {
    private final LinkedList<Worker<ChatMessage>> workers = new LinkedList<>();

    public ChatMessage process(ChatMessage chatMessage) {
        for (Worker<ChatMessage> worker : workers) {
            chatMessage = worker.getFunction().apply(chatMessage);

            if (chatMessage.isCancelled()) break;
        }

        return chatMessage;
    }

    @Override
    public void addWorker(Worker<ChatMessage> worker) {
        if (workers.stream().noneMatch(w -> w.getPriority() == worker.getPriority())) {
            workers.add(worker);
            workers.sort(Comparator.comparingInt(Worker::getPriority));
        }
    }

    @Override
    public boolean removeWorker(Worker<ChatMessage> worker) {
        if (!worker.isRemovable()) return false;
        return workers.remove(worker);
    }
}
