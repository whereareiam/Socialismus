package me.whereareiam.socialismus;

import com.google.inject.Guice;
import me.whereareiam.socialismus.listener.SpigotListenerRegistrar;

public final class SocialismusSpigot extends SocialismusBase {

    @Override
    public void onEnable() {
        injector = Guice.createInjector(new SocialismusSpigotConfig(this));
        injector.getInstance(SpigotListenerRegistrar.class).registerListeners();

        super.onEnable();
    }
}