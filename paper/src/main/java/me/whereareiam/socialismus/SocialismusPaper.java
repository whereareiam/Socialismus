package me.whereareiam.socialismus;

import com.google.inject.Guice;
import me.whereareiam.socialismus.listener.PaperListenerRegistrar;

public final class SocialismusPaper extends SocialismusBase {

    @Override
    public void onEnable() {
        injector = Guice.createInjector(new SocialismusPaperConfig(this));
        super.onEnable();

        injector.getInstance(PaperListenerRegistrar.class).registerListeners();
    }
}