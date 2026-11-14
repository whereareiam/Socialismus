package me.whereareiam.socialismus.module.mentionator.common.config.provider;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import me.whereareiam.socialismus.Reloadable;
import me.whereareiam.socialismus.config.PhrasesConfigProvider;
import me.whereareiam.socialismus.module.mentionator.common.config.defaults.MentionatorPhrasesDefaults;
import me.whereareiam.socialismus.registry.base.Registry;

import java.nio.file.Path;

@Singleton
public class MentionatorPhrasesProvider extends PhrasesConfigProvider {
	@Inject
	public MentionatorPhrasesProvider(@Named("workingPath") Path workingPath, Registry<Reloadable> reloadableRegistry) {
		super(workingPath, reloadableRegistry, MentionatorPhrasesDefaults.PHRASES);
	}
}
