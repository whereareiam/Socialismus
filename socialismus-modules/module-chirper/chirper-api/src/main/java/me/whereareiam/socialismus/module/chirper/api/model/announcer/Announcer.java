package me.whereareiam.socialismus.module.chirper.api.model.announcer;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import me.whereareiam.socialismus.module.chirper.api.type.OrderType;

import java.util.List;

@Getter
@ToString
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class Announcer {
    private boolean enabled;
    private List<String> announcements;
    private AnnouncerSettings settings;

    @Getter
    @ToString
    @NoArgsConstructor
    @SuperBuilder(toBuilder = true)
    public static class AnnouncerSettings {
        private OrderType order;
        private int minPlayers;
        private int interval;
        private int delay;
    }
}
