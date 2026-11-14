package me.whereareiam.socialismus.common.style;

import com.google.inject.Inject;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.lifecycle.RuntimeLifecycle;
import me.whereareiam.socialismus.lifecycle.StartupPhase;
import org.jetbrains.annotations.NotNull;

/**
 * Binds the styles once the modules have registered their condition types, so a style's requirements may name one.
 * A style that cannot be read stops the startup with its path.
 */
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public final class StyleLifecycle implements RuntimeLifecycle {
	private final DefaultStyles styles;

	@Override
	public @NotNull StartupPhase phase() {
		return StartupPhase.CONDITIONS;
	}

	@Override
	public void initialize() {
		styles.start();
	}

	@Override
	public void shutdown() {
		styles.stop();
	}
}
