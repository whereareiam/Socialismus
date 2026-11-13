package me.whereareiam.socialismus.module.chirper;

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
import me.whereareiam.socialismus.module.chirper.command.CommandRegistrar;
import me.whereareiam.socialismus.module.chirper.common.AnnouncerController;
import me.whereareiam.socialismus.module.chirper.common.CommonConfiguration;
import me.whereareiam.socialismus.module.chirper.common.condition.AnnouncementConditions;
import me.whereareiam.socialismus.module.chirper.common.config.migration.ChirperConfigMigrations;
import me.whereareiam.socialismus.registry.PlayerRegistry;
import me.whereareiam.socialismus.registry.base.Registry;
import me.whereareiam.socialismus.service.CommandService;
import me.whereareiam.socialismus.service.PlatformInteractor;
import me.whereareiam.socialismus.service.Scheduler;
import me.whereareiam.strata.Strata;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.List;

@SuppressWarnings("unused")
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class Chirper implements SocialisticModule {
	private final Injector parentInjector;
	private final Registry<Reloadable> reloadableRegistry;
	private Injector injector;

	@Override
	public void enable(@NotNull ModuleContext context) {
		Path dataFolder = context.dataFolder();
		new Strata(List.of(ChirperConfigMigrations.on(Config.configured(), dataFolder))).migrate();

		injector = Guice.createInjector(
				new ChirperInjectorConfiguration(
						parentInjector.getInstance(Scheduler.class),
						parentInjector.getInstance(PlatformInteractor.class),
						reloadableRegistry,
						parentInjector.getInstance(EventManager.class),
						parentInjector.getInstance(Conditions.class),
						parentInjector.getInstance(CommandService.class),
						parentInjector.getInstance(PlayerRegistry.class)
				),
				new CommonConfiguration(dataFolder)
		);

		// An announcement whose conditions cannot be bound stops the module, naming the list.
		AnnouncementConditions conditions = injector.getInstance(AnnouncementConditions.class);
		conditions.bind();
		injector.getInstance(EventManager.class).register(conditions);

		injector.getInstance(CommandRegistrar.class).registerCommands();
		injector.getInstance(AnnouncerController.class).init();
	}

	@Override
	public void disable() {
		injector.getInstance(EventManager.class).unregister(injector.getInstance(AnnouncerController.class));
		injector.getInstance(Scheduler.class).cancelByModule("chirper");
		injector.getInstance(EventManager.class).unregister(injector.getInstance(AnnouncementConditions.class));
		injector.getInstance(AnnouncementConditions.class).close();
	}
}
