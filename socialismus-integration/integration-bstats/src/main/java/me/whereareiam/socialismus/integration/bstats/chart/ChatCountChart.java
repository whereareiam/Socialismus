package me.whereareiam.socialismus.integration.bstats.chart;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.service.container.ChatContainerService;
import org.bstats.charts.CustomChart;
import org.bstats.charts.SimplePie;

@Singleton
public class ChatCountChart implements Chart {
    private final ChatContainerService containerService;

    @Inject
    public ChatCountChart(ChatContainerService containerService) {
        this.containerService = containerService;
    }

    public CustomChart getChart() {
        return new SimplePie("chatCount", this::getData);
    }

    private String getData() {
        return containerService.getChats().isEmpty() ? "NONE" : String.valueOf(containerService.getChats().size());
    }
}
