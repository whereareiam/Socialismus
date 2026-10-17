package me.whereareiam.socialismus.common.integration;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import me.whereareiam.socialismus.common.provider.IntegrationProvider;
import me.whereareiam.socialismus.event.EventListener;
import me.whereareiam.socialismus.event.EventManager;
import me.whereareiam.socialismus.event.base.SocialisticEvent;
import me.whereareiam.socialismus.event.plugin.PluginReloadedEvent;
import me.whereareiam.socialismus.integration.Integration;
import me.whereareiam.socialismus.lifecycle.RuntimeLifecycle;
import me.whereareiam.socialismus.lifecycle.StartupPhase;
import me.whereareiam.socialismus.type.IntegrationScope;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

/**
 * Starts the integrations with the other plugins on the server before the core, which builds on them: the serializer
 * takes the placeholders of PlaceholderAPI, for example. After a reload the {@link IntegrationScope#RELOADABLE}
 * integrations start again.
 */
@Singleton
public class IntegrationLifecycle implements RuntimeLifecycle, EventListener {
	private final IntegrationProvider integrationProvider;
	private final Set<Integration> integrationCandidates;
	private final EventManager eventManager;

	@Inject
	public IntegrationLifecycle(
			@NotNull IntegrationProvider integrationProvider,
			@NotNull @Named("integrationCandidates") Set<Integration> integrationCandidates,
			@NotNull EventManager eventManager
	) {
		this.integrationProvider = integrationProvider;
		this.integrationCandidates = integrationCandidates;
		this.eventManager = eventManager;
	}

	@Override
	public @NotNull StartupPhase phase() {
		return StartupPhase.INTEGRATIONS;
	}

	@Override
	public void initialize() {
		bootstrap(IntegrationScope.STARTUP_ONLY, IntegrationScope.RELOADABLE);
		eventManager.register(this);
	}

	@Override
	public void shutdown() {
		eventManager.unregister(this);
	}

	@SocialisticEvent
	public void onReloaded(PluginReloadedEvent event) {
		bootstrap(IntegrationScope.RELOADABLE);
	}

	private void bootstrap(IntegrationScope... scopes) {
		Set<IntegrationScope> allowed = EnumSet.copyOf(Arrays.asList(scopes));
		for (Integration integration : integrationCandidates) {
			if (integration == null) continue;
			if (!allowed.contains(integration.scope())) continue;
			integration.initialize(integrationProvider);
		}
	}
}
