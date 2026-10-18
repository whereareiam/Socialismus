package me.whereareiam.socialismus.module.dialogue.common.config.provider;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import me.whereareiam.configura.Configura;
import me.whereareiam.socialismus.Reloadable;
import me.whereareiam.socialismus.config.ConfigProvider;
import me.whereareiam.socialismus.module.dialogue.api.model.config.MessagingFormats;
import me.whereareiam.socialismus.module.dialogue.common.config.defaults.MessagingFormatsDefaults;
import me.whereareiam.socialismus.registry.base.Registry;

import java.nio.file.Path;

@Singleton
public class MessagingFormatsProvider extends ConfigProvider<MessagingFormats> {
	@Inject
	public MessagingFormatsProvider(
			@Named("formatsPath") Path formatsPath,
			Registry<Reloadable> reloadableRegistry
	) {
		super(formatsPath, "messaging", MessagingFormats.class, reloadableRegistry);
	}

	@Override
	protected Configura configura() {
		return super.configura().withDefaults(MessagingFormatsDefaults.class);
	}
}
