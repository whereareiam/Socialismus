package me.whereareiam.socialismus.module.bubbler.common.config.provider;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import me.whereareiam.configura.Configura;
import me.whereareiam.socialismus.Constants;
import me.whereareiam.socialismus.Reloadable;
import me.whereareiam.socialismus.config.ConfigProvider;
import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.module.bubbler.api.model.config.BubblerSettings;
import me.whereareiam.socialismus.module.bubbler.api.type.BubbleType;
import me.whereareiam.socialismus.module.bubbler.common.config.defaults.BubblerSettingsDefaults;
import me.whereareiam.socialismus.registry.base.Registry;
import me.whereareiam.socialismus.type.Version;

import java.nio.file.Path;

@Singleton
public class BubblerSettingsProvider extends ConfigProvider<BubblerSettings> {
    @Inject
    public BubblerSettingsProvider(
            @Named("workingPath") Path workingPath,
            Registry<Reloadable> reloadableRegistry
    ) {
        super(workingPath, "settings", BubblerSettings.class, reloadableRegistry);
    }

    @Override
    protected BubblerSettings load() {
        BubblerSettings settings = super.load();
        // Text displays came with Minecraft 1.19.4; older clients are sent nothing they can show.
        if (settings.getBubbleType() == BubbleType.TEXT_DISPLAY
                && Constants.SERVER_VERSION != Version.UNKNOWN
                && Version.isLowerThan(Constants.SERVER_VERSION, Version.V_1_19_4))
            Logger.warn("Text displays need Minecraft 1.19.4 or newer, so Bubbler shows no bubbles on this server;"
                    + " set bubbleType to ARMOR_STAND in modules/bubbler/settings.yml.");

        return settings;
    }

    @Override
    protected Configura configura() {
        return super.configura().withDefaults(BubblerSettingsDefaults.class);
    }
}
