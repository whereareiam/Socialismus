package me.whereareiam.socialismus.spigot;

import com.google.inject.Guice;
import me.whereareiam.socialismus.core.AbstractSocialismus;
import me.whereareiam.socialismus.spigot.listener.SpigotListenerRegistrar;

public final class SocialismusSpigot extends AbstractSocialismus {
	@Override
	public void onEnable() {
		injector = Guice.createInjector(new SocialismusSpigotConfig(this, this));
		super.onEnable();

		injector.getInstance(SpigotListenerRegistrar.class).registerListeners();
	}
}