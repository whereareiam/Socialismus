package me.whereareiam.socialismus.module.bubbler.common;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Named;
import me.whereareiam.configura.feature.polymorphic.PolymorphicFeature;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.module.bubbler.api.BubbleCoordinationService;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.Bubble;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.BubbleMessage;
import me.whereareiam.socialismus.module.bubbler.api.model.config.BubblerCommands;
import me.whereareiam.socialismus.module.bubbler.api.model.config.BubblerMessages;
import me.whereareiam.socialismus.module.bubbler.api.model.config.BubblerSettings;
import me.whereareiam.socialismus.module.bubbler.api.model.requirement.ActivatorRequirement;
import me.whereareiam.socialismus.module.bubbler.common.config.provider.BubblerCommandsProvider;
import me.whereareiam.socialismus.module.bubbler.common.config.provider.BubblerMessagesProvider;
import me.whereareiam.socialismus.module.bubbler.common.config.provider.BubblerSettingsProvider;
import me.whereareiam.socialismus.module.bubbler.common.config.provider.BubblesProvider;
import me.whereareiam.socialismus.module.bubbler.common.processor.BubbleMessageProcessor;
import me.whereareiam.socialismus.module.bubbler.common.worker.BubbleSelector;
import me.whereareiam.socialismus.module.bubbler.common.worker.container.ContentArranger;
import me.whereareiam.socialismus.module.bubbler.common.worker.container.ContentTimeEvaluator;
import me.whereareiam.socialismus.module.bubbler.common.requirement.ActivatorRequirementValidation;
import me.whereareiam.socialismus.module.bubbler.common.worker.recipient.RecipientResolver;
import me.whereareiam.socialismus.module.bubbler.common.worker.recipient.RecipientSelector;
import me.whereareiam.socialismus.model.requirement.Requirement;
import me.whereareiam.socialismus.registry.WorkerProcessor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@RequiredArgsConstructor
public class CommonConfiguration extends AbstractModule {
	private final Path workingPath;

	@Override
	protected void configure() {
		requestInjection(this);

		// Configuration
		bind(BubblerSettingsProvider.class).asEagerSingleton();
		bind(BubblerSettings.class).toProvider(BubblerSettingsProvider.class);

		bind(BubblerMessagesProvider.class).asEagerSingleton();
		bind(BubblerMessages.class).toProvider(BubblerMessagesProvider.class);

		bind(BubblerCommandsProvider.class).asEagerSingleton();
		bind(BubblerCommands.class).toProvider(BubblerCommandsProvider.class);

		bind(BubblesProvider.class).asEagerSingleton();
		bind(new TypeLiteral<List<Bubble>>() {}).toProvider(BubblesProvider.class);

		// General
		bind(BubbleCoordinationService.class).to(BubbleCoordinator.class);
		bind(new TypeLiteral<WorkerProcessor<BubbleMessage>>() {}).to(BubbleMessageProcessor.class);

		bind(RecipientResolver.class).asEagerSingleton();
		bind(RecipientSelector.class).asEagerSingleton();
		bind(BubbleSelector.class).asEagerSingleton();
		bind(ContentArranger.class).asEagerSingleton();
		bind(ContentTimeEvaluator.class).asEagerSingleton();

		// Requirements
		bind(ActivatorRequirementValidation.class).asEagerSingleton();
	}

	public static PolymorphicFeature createRequirementPolymorphicFeature() {
		PolymorphicFeature polymorphic = PolymorphicFeature.defaults();
		polymorphic.register(Requirement.class)
				.inferByField("activators", ActivatorRequirement.class)
				.build();
		return polymorphic;
	}

	@Provides
	@Singleton
	@Named("workingPath")
	Path provideWorkingPath() {
		return ensureDirectory(workingPath, "workingPath");
	}

	@Provides
	@Singleton
	@Named("bubblesPath")
	Path provideBubblesPath() {
		return ensureDirectory(workingPath.resolve("bubbles"), "bubblesPath");
	}

	private Path ensureDirectory(Path path, String label) {
		try {
			Files.createDirectories(path);
			return path;
		} catch (IOException e) {
			throw new RuntimeException("Failed to create " + label + " directory", e);
		}
	}
}
