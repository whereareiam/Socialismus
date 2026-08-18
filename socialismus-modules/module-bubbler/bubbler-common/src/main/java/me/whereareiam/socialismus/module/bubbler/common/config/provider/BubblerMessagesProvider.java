package me.whereareiam.socialismus.module.bubbler.common.config.provider;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import me.whereareiam.configura.Configura;
import me.whereareiam.socialismus.Reloadable;
import me.whereareiam.socialismus.config.ConfigProvider;
import me.whereareiam.socialismus.module.bubbler.api.model.config.BubblerMessages;
import me.whereareiam.socialismus.module.bubbler.common.config.defaults.BubblerMessagesDefaults;
import me.whereareiam.socialismus.registry.base.Registry;

import java.nio.file.Path;

@Singleton
public class BubblerMessagesProvider extends ConfigProvider<BubblerMessages> {
    @Inject
    public BubblerMessagesProvider(
            @Named("workingPath") Path workingPath,
            Registry<Reloadable> reloadableRegistry
    ) {
        super(workingPath, "messages", BubblerMessages.class, reloadableRegistry);
    }

    @Override
    protected Configura configura() {
        return versioned(super.configura().withDefaults(BubblerMessagesDefaults.class), BubblerMessages.class);
    }
}
