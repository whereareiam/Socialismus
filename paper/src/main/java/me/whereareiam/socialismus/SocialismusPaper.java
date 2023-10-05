package me.whereareiam.socialismus;

import me.whereareiam.socialismus.listener.PaperListenerRegistrar;

public final class SocialismusPaper extends SocialismusBase {

    @Override
    public void onEnable() {
        super.onEnable();

        injector.getInstance(PaperListenerRegistrar.class).registerListeners();
    }
}