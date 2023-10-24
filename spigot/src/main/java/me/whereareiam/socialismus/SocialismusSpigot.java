package me.whereareiam.socialismus;

import me.whereareiam.socialismus.listener.SpigotListenerRegistrar;
import me.whereareiam.socialismus.util.MessageUtil;

public final class SocialismusSpigot extends SocialismusBase {

    @Override
    public void onEnable() {
        super.onEnable();

        injector.getInstance(SpigotListenerRegistrar.class).registerListeners();
    }

    @Override
    public void onDisable() {
        injector.getInstance(MessageUtil.class).close();
    }
}