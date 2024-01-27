package me.whereareiam.socialismus.paper;

import com.google.inject.Guice;
import me.whereareiam.socialismus.core.AbstractSocialismus;
import me.whereareiam.socialismus.paper.listener.PaperListenerRegistrar;

public final class SocialismusPaper extends AbstractSocialismus {
	@Override
	public void onEnable() {
		injector = Guice.createInjector(new SocialismusPaperConfig(this, this));
		super.onEnable();

		injector.getInstance(PaperListenerRegistrar.class).registerListeners();
	}
}