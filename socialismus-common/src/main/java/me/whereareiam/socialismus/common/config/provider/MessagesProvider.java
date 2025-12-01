package me.whereareiam.socialismus.common.config.provider;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import me.whereareiam.configura.Config;
import me.whereareiam.socialismus.Reloadable;
import me.whereareiam.socialismus.common.config.template.MessagesTemplate;
import me.whereareiam.socialismus.model.config.message.Messages;
import me.whereareiam.socialismus.registry.Registry;

import java.nio.file.Path;

@Singleton
public class MessagesProvider extends DefaultConfigProvider<Messages> {
	@Inject
	public MessagesProvider(@Named("dataPath") Path dataPath, Registry<Reloadable> registry) {
		super(dataPath, registry);
	}

	@Override
	protected Messages load() {
		return Config.update(getBasePath().resolve("messages"), Messages.class);
	}

	@Override
	protected void registerTemplate() {
		Config.registerTemplate(MessagesTemplate.class);
	}
}