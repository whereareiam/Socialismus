package me.whereareiam.socialismus.module.bubbler.api.model.config;

import com.google.inject.Singleton;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Singleton
public class BubblerMessages {
    private String noPlayers;
    private String noNearbyPlayers;
    private String noBubbleSelected;

    private String messageSuccess;
}
