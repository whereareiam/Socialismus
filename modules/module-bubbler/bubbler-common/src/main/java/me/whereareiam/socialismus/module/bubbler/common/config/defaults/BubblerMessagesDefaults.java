package me.whereareiam.socialismus.module.bubbler.common.config.defaults;

import com.google.inject.Singleton;
import me.whereareiam.configura.merge.defaults.DefaultsProvider;
import me.whereareiam.socialismus.module.bubbler.api.model.config.BubblerMessages;

@Singleton
public class BubblerMessagesDefaults implements DefaultsProvider<BubblerMessages> {
    @Override
    public BubblerMessages supply(BubblerMessages config) {
        // Default values
        config.setNoPlayers("{prefix}<red>There are no players online to send a bubble message to.");
        config.setNoNearbyPlayers("{prefix}<red>There are no players nearby to send a bubble message to.");
        config.setNoBubbleSelected("{prefix}<red>There are no bubbles available to send a message with.");
        config.setMessageSuccess("{prefix}<white>Successfully sent a bubble message.");

        return config;
    }
}
