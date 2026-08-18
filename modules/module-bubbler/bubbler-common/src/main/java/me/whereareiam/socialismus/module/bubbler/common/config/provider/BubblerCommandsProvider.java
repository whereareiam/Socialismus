package me.whereareiam.socialismus.module.bubbler.common.config.provider;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import me.whereareiam.configura.Configura;
import me.whereareiam.socialismus.Reloadable;
import me.whereareiam.socialismus.config.ConfigProvider;
import me.whereareiam.socialismus.module.bubbler.api.model.config.BubblerCommands;
import me.whereareiam.socialismus.module.bubbler.common.config.defaults.BubblerCommandsDefaults;
import me.whereareiam.socialismus.registry.base.Registry;

import java.nio.file.Path;

@Singleton
public class BubblerCommandsProvider extends ConfigProvider<BubblerCommands> {
    @Inject
    public BubblerCommandsProvider(
            @Named("workingPath") Path workingPath,
            Registry<Reloadable> reloadableRegistry
    ) {
        super(workingPath, "commands", BubblerCommands.class, reloadableRegistry);
    }

    @Override
    protected Configura configura() {
        return versioned(super.configura().withDefaults(BubblerCommandsDefaults.class), BubblerCommands.class);
    }
}
