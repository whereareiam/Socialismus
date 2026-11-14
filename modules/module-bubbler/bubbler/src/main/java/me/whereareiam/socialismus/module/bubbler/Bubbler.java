package me.whereareiam.socialismus.module.bubbler;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import lombok.RequiredArgsConstructor;
import me.whereareiam.conditia.Conditions;
import me.whereareiam.configura.Config;
import me.whereareiam.socialismus.Reloadable;
import me.whereareiam.socialismus.event.EventManager;
import me.whereareiam.socialismus.module.ModuleContext;
import me.whereareiam.socialismus.module.SocialisticModule;
import me.whereareiam.socialismus.module.bubbler.command.CommandRegistrar;
import me.whereareiam.socialismus.module.bubbler.common.CommonConfiguration;
import me.whereareiam.socialismus.module.bubbler.common.condition.ActivatorCondition;
import me.whereareiam.socialismus.module.bubbler.common.condition.BubbleConditions;
import me.whereareiam.socialismus.module.bubbler.common.condition.ChatActivationCondition;
import me.whereareiam.socialismus.module.bubbler.common.config.migration.BubblerConfigMigrations;
import me.whereareiam.socialismus.module.bubbler.common.listener.BubbleTransitionListener;
import me.whereareiam.socialismus.module.bubbler.common.listener.ChatBroadcastListener;
import me.whereareiam.socialismus.registry.PlayerRegistry;
import me.whereareiam.socialismus.registry.base.Registry;
import me.whereareiam.socialismus.service.CommandService;
import me.whereareiam.socialismus.service.PlatformInteractor;
import me.whereareiam.socialismus.service.Scheduler;
import me.whereareiam.socialismus.service.style.Styles;
import me.whereareiam.strata.Strata;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

@SuppressWarnings("unused")
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class Bubbler implements SocialisticModule {
	private final Injector parentInjector;
	private final Registry<Reloadable> reloadableRegistry;
	private Injector injector;

	@Override
	public void enable(@NotNull ModuleContext context) {
		Path dataFolder = context.dataFolder();
		new Strata(List.of(BubblerConfigMigrations.on(Config.configured(), dataFolder))).migrate();

		EventManager eventManager = parentInjector.getInstance(EventManager.class);
		context.conditions().register(new ActivatorCondition());
		context.conditions().register(new ChatActivationCondition());

		injector =
				Guice.createInjector(
						new BubblerInjectorConfiguration(
								parentInjector.getInstance(Scheduler.class),
								parentInjector.getInstance(PlatformInteractor.class),
								reloadableRegistry,
								parentInjector.getInstance(Conditions.class),
								parentInjector.getInstance(CommandService.class),
								parentInjector.getInstance(PlayerRegistry.class),
								eventManager,
								parentInjector.getInstance(Styles.class)
						),
						new CommonConfiguration(dataFolder));

		// A bubble whose bubbleConditions cannot be bound stops the module, naming the list.
		BubbleConditions bubbleConditions = injector.getInstance(BubbleConditions.class);
		bubbleConditions.bind();

		Stream.of(
				bubbleConditions,
				injector.getInstance(ChatBroadcastListener.class),
				injector.getInstance(BubbleTransitionListener.class)
		).forEach(eventManager::register);

		injector.getInstance(CommandRegistrar.class).registerCommands();
	}

	@Override
	public void disable() {
		injector.getInstance(Scheduler.class).cancelByModule("bubbler");
		injector.getInstance(BubbleConditions.class).close();
	}
}
