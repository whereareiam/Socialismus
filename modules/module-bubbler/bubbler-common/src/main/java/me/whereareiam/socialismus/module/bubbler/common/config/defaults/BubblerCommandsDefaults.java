package me.whereareiam.socialismus.module.bubbler.common.config.defaults;

import com.google.inject.Singleton;
import me.whereareiam.configura.merge.defaults.DefaultsProvider;
import me.whereareiam.socialismus.model.CommandDefinition;
import me.whereareiam.socialismus.module.bubbler.api.model.config.BubblerCommands;

import java.util.List;
import java.util.Map;

@Singleton
public class BubblerCommandsDefaults implements DefaultsProvider<BubblerCommands> {
    @Override
    public BubblerCommands supply(BubblerCommands config) {
        // Default values
        CommandDefinition bubble = CommandDefinition.builder()
                .enabled(true)
                .aliases(List.of("bubble"))
                .permission("socialismus.admin")
                .description("Bubble message command")
                .usage("{command} {alias} <message>")
                .cooldown(CommandDefinition.Cooldown.builder()
                        .enabled(true)
                        .duration(2)
                        .group("global")
                        .build()
                )
                .arguments(Map.of(
                        "message", "Message"
                )).build();

        config.getCommands().put("bubble", bubble);

        return config;
    }
}
