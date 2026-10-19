package me.whereareiam.socialismus.common;

import com.google.inject.Inject;
import me.whereareiam.socialismus.common.printer.WelcomeBannerPrinter;
import me.whereareiam.socialismus.lifecycle.RuntimeLifecycle;
import me.whereareiam.socialismus.lifecycle.StartupPhase;
import me.whereareiam.socialismus.listener.ListenerRegistrar;
import org.jetbrains.annotations.NotNull;

/**
 * Lets Socialismus handle players as the last step of its startup: registers the platform's listeners and prints the
 * welcome banner.
 */
public final class ConnectionLifecycle implements RuntimeLifecycle {
	private final ListenerRegistrar listenerRegistrar;
	private final WelcomeBannerPrinter bannerPrinter;

	@Inject
	public ConnectionLifecycle(
			@NotNull ListenerRegistrar listenerRegistrar,
			@NotNull WelcomeBannerPrinter bannerPrinter
	) {
		this.listenerRegistrar = listenerRegistrar;
		this.bannerPrinter = bannerPrinter;
	}

	@Override
	public @NotNull StartupPhase phase() {
		return StartupPhase.CONNECTIONS;
	}

	@Override
	public void initialize() {
		listenerRegistrar.registerListeners();
		bannerPrinter.print();
	}

	/**
	 * Leaves the platform's listeners registered: the server removes them with the plugin.
	 */
	@Override
	public void shutdown() {
	}
}
