package me.whereareiam.socialismus.module.bubbler;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.Reloadable;
import me.whereareiam.socialismus.event.EventManager;
import me.whereareiam.socialismus.module.SocialisticModule;
import me.whereareiam.socialismus.module.bubbler.command.CommandRegistrar;
import me.whereareiam.socialismus.module.bubbler.common.CommonConfiguration;
import me.whereareiam.socialismus.module.bubbler.common.listener.BubbleTransitionListener;
import me.whereareiam.socialismus.module.bubbler.common.listener.ChatBroadcastListener;
import me.whereareiam.socialismus.registry.PlayerRegistry;
import me.whereareiam.socialismus.registry.base.Registry;
import me.whereareiam.socialismus.service.CommandService;
import me.whereareiam.socialismus.service.PlatformInteractor;
import me.whereareiam.socialismus.service.Scheduler;
import me.whereareiam.socialismus.service.requirement.RequirementEvaluatorService;

import java.util.stream.Stream;

@SuppressWarnings("unused")
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class Bubbler extends SocialisticModule {
	private final Injector parentInjector;
	private final Registry<Reloadable> reloadableRegistry;
	private Injector injector;

	@Override
	public void onLoad() {
		EventManager eventManager = parentInjector.getInstance(EventManager.class);

		injector =
				Guice.createInjector(
						new BubblerInjectorConfiguration(
								parentInjector.getInstance(Scheduler.class),
								parentInjector.getInstance(PlatformInteractor.class),
								reloadableRegistry,
								parentInjector.getInstance(RequirementEvaluatorService.class),
								parentInjector.getInstance(Key.get(new TypeLiteral<>() {})),
								parentInjector.getInstance(CommandService.class),
								parentInjector.getInstance(PlayerRegistry.class),
								eventManager
						),
						new CommonConfiguration(workingPath));

		Stream.of(
				injector.getInstance(ChatBroadcastListener.class),
				injector.getInstance(BubbleTransitionListener.class)
		).forEach(eventManager::register);
	}

	@Override
	public void onEnable() {
		injector.getInstance(CommandRegistrar.class).registerCommands();
	}

	@Override
	public void onDisable() {
		injector.getInstance(Scheduler.class).cancelByModule("bubbler");
	}

	@Override
	public void onUnload() {
	}
}
