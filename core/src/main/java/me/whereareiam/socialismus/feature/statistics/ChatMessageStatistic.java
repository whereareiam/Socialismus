package me.whereareiam.socialismus.feature.statistics;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.util.LoggerUtil;
import org.bukkit.plugin.Plugin;

@Singleton
public class ChatMessageStatistic extends Statistic<Integer> {

    @Inject
    public ChatMessageStatistic(LoggerUtil loggerUtil, Plugin plugin) {
        super(loggerUtil, plugin, "chats.stat");
    }

    public void incrementStatistic(String chatId) {
        int count = statistics.getOrDefault(chatId, 0);
        statistics.put(chatId, count + 1);
    }

    public int getMessageCount(String chatId) {
        return statistics.getOrDefault(chatId, 0);
    }

    @Override
    protected Integer parseValue(String value) {
        return Integer.parseInt(value);
    }

    @Override
    protected String formatValue(Integer value) {
        return value.toString();
    }
}
