package me.whereareiam.socialismus.integration.papiproxybridge;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.output.integration.Integration;
import me.whereareiam.socialismus.output.integration.PlaceholderResolverIntegration;
import me.whereareiam.socialismus.registry.Registry;
import net.william278.papiproxybridge.api.PlaceholderAPI;

@Singleton
public class PAPIProxyBridgeIntegration implements PlaceholderResolverIntegration {
    private Object placeholderAPI;

    @Inject
    public PAPIProxyBridgeIntegration(Registry<Integration> registry) {
        if (!isAvailable()) return;

        this.placeholderAPI = PlaceholderAPI.createInstance();
        registry.register(this);
    }

    @Override
    public String format(SocialismusPlayer socialismusPlayer, String content) {
	    return ((PlaceholderAPI) placeholderAPI).formatPlaceholders(content, socialismusPlayer.getUniqueId()).getNow(content);
    }

    @Override
    public String getName() {
        return "PAPIProxyBridge";
    }

    @Override
    public boolean isAvailable() {
        try {
            Class.forName("net.william278.papiproxybridge.api.PlaceholderAPI");

            return true;
        } catch (ClassNotFoundException | NoClassDefFoundError e) {
            return false;
        }
    }
}
