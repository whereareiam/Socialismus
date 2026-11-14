package me.whereareiam.socialismus.module.mentionator.common.config.provider;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import me.whereareiam.configura.Configura;
import me.whereareiam.socialismus.Reloadable;
import me.whereareiam.socialismus.config.ConfigProvider;
import me.whereareiam.socialismus.module.mentionator.api.model.config.MentionatorMessages;
import me.whereareiam.socialismus.module.mentionator.common.config.defaults.MentionatorMessagesDefaults;
import me.whereareiam.socialismus.registry.base.Registry;

import java.nio.file.Path;

@Singleton
public class MentionatorMessagesProvider extends ConfigProvider<MentionatorMessages> {
	@Inject
	public MentionatorMessagesProvider(@Named("workingPath") Path workingPath, Registry<Reloadable> reloadableRegistry) {
		super(workingPath, "messages", MentionatorMessages.class, reloadableRegistry);
	}

	@Override
	protected Configura configura() {
		return super.configura().withDefaults(MentionatorMessagesDefaults.class);
	}
}
