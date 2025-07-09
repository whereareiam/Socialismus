package me.whereareiam.socialismus.command.management;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.model.CommandEntity;
import me.whereareiam.socialismus.api.model.player.DummyPlayer;
import me.whereareiam.socialismus.api.output.command.CommandBase;
import me.whereareiam.socialismus.api.output.command.CommandCooldown;
import me.whereareiam.socialismus.api.output.command.CommandService;
import me.whereareiam.socialismus.command.executor.*;
import me.whereareiam.socialismus.command.listener.CommandCooldownListener;
import me.whereareiam.socialismus.command.provider.CrossPlayerProvider;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.execution.postprocessor.CommandPostprocessor;
import org.incendo.cloud.processors.cooldown.*;
import org.incendo.cloud.processors.cooldown.annotation.CooldownBuilderModifier;
import org.incendo.cloud.processors.cooldown.listener.ScheduledCleanupCreationListener;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Stream;

@Singleton
public class CommandProcessor implements CommandService {
	private final Injector injector;
	private final Provider<CommandManager<DummyPlayer>> commandManager;
	private final AnnotationParser<DummyPlayer> annotationParser;

	private final Map<String, String> translations = new HashMap<>();

	@Inject
	public CommandProcessor(
			Injector injector,
			Provider<CommandManager<DummyPlayer>> commandManager,
			Provider<Map<String, CommandEntity>> commands
	) {
		this.injector = injector;
		this.commandManager = commandManager;
		this.annotationParser = new AnnotationParser<>(commandManager.get(), DummyPlayer.class);

		annotationParser.stringProcessor(injector.getInstance(CommandTranslator.class).getProcessor());
		annotationParser.registerBuilderModifier(
				CommandCooldown.class,
				((annotation, builder) -> {
					CommandEntity entity = commands.get().get(annotation.value().split("\\.")[1]);
					if (entity == null || !entity.getCooldown().isEnabled()) return builder;

					Cooldown<DummyPlayer> cooldown = Cooldown.of(
							DurationFunction.constant(Duration.ofSeconds(entity.getCooldown().getDuration())),
							CooldownGroup.named(entity.getCooldown().getGroup())
					);

					return builder.apply(cooldown);
				})
		);
		annotationParser.parse(injector.getInstance(CrossPlayerProvider.class));

		CooldownBuilderModifier.install(annotationParser);

		commandManager.get().registerCommandPostProcessor(createCooldownManager());
	}

	@Override
	public void initialize() {
		CommandManager<DummyPlayer> commandManager = this.commandManager.get();

		commandManager.rootCommands().forEach(commandManager::deleteRootCommand);
		Stream.of(injector.getInstance(MainCommand.class),
				injector.getInstance(HelpCommand.class),
				injector.getInstance(DebugCommand.class),
				injector.getInstance(ReloadCommand.class),
				injector.getInstance(ClearCommand.class)
		).forEach(this::registerCommand);
	}

	private CommandPostprocessor<DummyPlayer> createCooldownManager() {
		CooldownRepository<DummyPlayer> repository = CooldownRepository.mapping(
				DummyPlayer::getUniqueId,
				CooldownRepository.forMap(new HashMap<>())
		);

		ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor(); // Will cause a leak if shutdown is executed by PlugMan or similar tools
		CooldownConfiguration<DummyPlayer> configuration = CooldownConfiguration.<DummyPlayer>builder()
				.repository(repository)
				.addActiveCooldownListener(injector.getInstance(CommandCooldownListener.class))
				.addCreationListener(new ScheduledCleanupCreationListener<>(executorService, repository))
				.build();

		CooldownManager<DummyPlayer> cooldownManager = CooldownManager.cooldownManager(
				configuration
		);

		return cooldownManager.createPostprocessor();
	}

	@Override
	public void registerCommand(CommandBase command) {
		translations.putAll(command.getTranslations());
		annotationParser.parse(command);
	}

	@Override
	public void registerTranslation(String key, String value) {
		translations.put(key, value);
	}

	@Override
	public int getCommandCount() {
		return commandManager.get().commands().size();
	}

	@Override
	public String getTranslation(String key) {
		return translations.get(key);
	}

	@Override
	public Map<String, String> getTranslations() {
		return translations;
	}
}


