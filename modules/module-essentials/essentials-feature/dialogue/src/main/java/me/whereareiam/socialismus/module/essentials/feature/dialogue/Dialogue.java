package me.whereareiam.socialismus.module.essentials.feature.dialogue;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.model.CommandDefinition;
import me.whereareiam.socialismus.module.essentials.api.model.feature.CommandFeature;
import me.whereareiam.socialismus.module.essentials.api.feature.FeatureInitializer;
import me.whereareiam.socialismus.module.essentials.feature.dialogue.command.MessageCommand;
import me.whereareiam.socialismus.module.essentials.feature.dialogue.command.ReplyCommand;
import me.whereareiam.socialismus.module.essentials.feature.dialogue.config.DialogueCommands;
import me.whereareiam.socialismus.module.essentials.feature.dialogue.config.DialogueMessages;
import me.whereareiam.socialismus.module.essentials.feature.dialogue.config.DialogueSettings;
import me.whereareiam.socialismus.module.essentials.feature.dialogue.provider.ReplyTargetProvider;
import me.whereareiam.socialismus.module.essentials.feature.dialogue.sync.DialogueNetworkBridge;
import me.whereareiam.socialismus.service.CommandService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class Dialogue implements FeatureInitializer<CommandFeature> {
	private final DialogueNetworkBridge networkBridge;
	private final CommandService commandService;
	private final Injector injector;

	private final Provider<DialogueSettings> settings;
	private final Provider<DialogueMessages> messages;
	private final Provider<DialogueCommands> commands;

	@Override
	public String getId() {
		return "dialogue";
	}

	@Override
	public void initialize(CommandFeature feature) {
		settings.get();
		messages.get();
		commands.get();

		networkBridge.initialize();

		if (feature.isRegisterCommands()) {
			registerCommands();
		}
	}

	private void registerCommands() {
		Map<String, CommandDefinition> commandDefs = commands.get().getCommands();
		Map<String, Class<?>> executors = Map.of(
				"message", MessageCommand.class,
				"reply", ReplyCommand.class,
				"replyTargetProvider", ReplyTargetProvider.class
		);

		List<Object> commandInstances = new ArrayList<>();
		for (Class<?> executorClass : executors.values()) {
			commandInstances.add(injector.getInstance(executorClass));
		}

		commandService.registerCommandInstances(commandDefs, commandInstances.toArray());
		Logger.info("Registered commands for feature: dialogue");
	}
}
