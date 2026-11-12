package me.whereareiam.socialismus.module.dialogue.common.config.provider;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import me.whereareiam.socialismus.Reloadable;
import me.whereareiam.socialismus.config.PhrasesConfigProvider;
import me.whereareiam.socialismus.module.dialogue.common.config.defaults.DialoguePhrasesDefaults;
import me.whereareiam.socialismus.registry.base.Registry;

import java.nio.file.Path;

@Singleton
public class DialoguePhrasesProvider extends PhrasesConfigProvider {
	@Inject
	public DialoguePhrasesProvider(@Named("workingPath") Path workingPath, Registry<Reloadable> reloadableRegistry) {
		super(workingPath, reloadableRegistry, DialoguePhrasesDefaults.PHRASES);
	}
}
