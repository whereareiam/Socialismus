package me.whereareiam.socialismus.common.config.provider;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import me.whereareiam.configura.Configura;
import me.whereareiam.socialismus.Reloadable;
import me.whereareiam.socialismus.common.config.defaults.MessagesDefaults;
import me.whereareiam.socialismus.model.config.message.Messages;
import me.whereareiam.socialismus.registry.base.Registry;

import java.nio.file.Path;

@Singleton
public class MessagesProvider extends DefaultConfigProvider<Messages> {
	@Inject
	public MessagesProvider(@Named("dataPath") Path dataPath, Registry<Reloadable> registry) {
		super(dataPath, "messages", Messages.class, registry);
	}

	@Override
	protected Configura configura() {
		return versioned(super.configura().withDefaults(MessagesDefaults.class), Messages.class);
	}
}
