package me.whereareiam.socialismus.integration.packetevents;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.output.integration.Integration;
import me.whereareiam.socialismus.registry.Registry;

@Singleton
public class PacketEventsIntegration implements Integration {
    @Inject
    public PacketEventsIntegration(Registry<Integration> registry) {
        if (!isAvailable()) return;

        registry.register(this);
    }

    @Override
    public String getName() {
        return "PacketEvents";
    }

    @Override
    public boolean isAvailable() {
        try {
            Class.forName("com.github.retrooper.packetevents.PacketEventsAPI");

            return true;
        } catch (ClassNotFoundException | NoClassDefFoundError e) {
            return false;
        }
    }
}
