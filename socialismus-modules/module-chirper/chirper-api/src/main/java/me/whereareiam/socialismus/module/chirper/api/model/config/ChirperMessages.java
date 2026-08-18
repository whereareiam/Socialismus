package me.whereareiam.socialismus.module.chirper.api.model.config;

import com.google.inject.Singleton;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Singleton
public class ChirperMessages {
    private String noPlayers;
    private String noAnnouncementFound;

    private String announcementBroadcasted;
}
