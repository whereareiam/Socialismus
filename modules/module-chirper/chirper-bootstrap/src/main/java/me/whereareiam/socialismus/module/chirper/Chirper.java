package me.whereareiam.socialismus.module.chirper;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.Reloadable;
import me.whereareiam.socialismus.module.SocialisticModule;
import me.whereareiam.socialismus.module.chirper.command.CommandRegistrar;
import me.whereareiam.socialismus.module.chirper.common.AnnouncerController;
import me.whereareiam.socialismus.module.chirper.common.CommonConfiguration;
import me.whereareiam.socialismus.registry.base.Registry;
import me.whereareiam.socialismus.service.CommandService;
import me.whereareiam.socialismus.service.PlatformInteractor;
import me.whereareiam.socialismus.service.Scheduler;
import me.whereareiam.socialismus.service.requirement.RequirementEvaluatorService;
import me.whereareiam.socialismus.registry.PlayerRegistry;

@SuppressWarnings("unused")
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class Chirper extends SocialisticModule {
	private final Injector parentInjector;
	private final Registry<Reloadable> reloadableRegistry;
	private Injector injector;

	@Override
	public void onLoad() {
		injector = Guice.createInjector(
				new ChirperInjectorConfiguration(
						parentInjector.getInstance(Scheduler.class),
						parentInjector.getInstance(PlatformInteractor.class),
						reloadableRegistry,
						parentInjector.getInstance(RequirementEvaluatorService.class),
						parentInjector.getInstance(CommandService.class),
						parentInjector.getInstance(PlayerRegistry.class)
				),
				new CommonConfiguration(workingPath)
		);
	}

	@Override
	public void onEnable() {
		injector.getInstance(CommandRegistrar.class).registerCommands();
		injector.getInstance(AnnouncerController.class).init();
	}

	@Override
	public void onDisable() {
		injector.getInstance(Scheduler.class).cancelByModule("chirper");
	}

	@Override
	public void onUnload() {

	}
}
