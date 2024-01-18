package me.whereareiam.socialismus.spigot;

import com.google.inject.Guice;
import me.whereareiam.socialismus.core.SocialismusBase;
import me.whereareiam.socialismus.spigot.listener.SpigotListenerRegistrar;

public final class SocialismusSpigot extends SocialismusBase {

	@Override
	public void onEnable() {
		injector = Guice.createInjector(new SocialismusSpigotConfig(this));
		super.onEnable();

		injector.getInstance(SpigotListenerRegistrar.class).registerListeners();
	}
}