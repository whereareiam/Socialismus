package me.whereareiam.socialismus.common.style;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import me.whereareiam.conditia.Conditions;
import me.whereareiam.socialismus.event.EventListener;
import me.whereareiam.socialismus.event.EventManager;
import me.whereareiam.socialismus.event.base.SocialisticEvent;
import me.whereareiam.socialismus.event.plugin.PluginReloadedEvent;
import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.model.config.Settings;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.model.style.Styles;
import me.whereareiam.socialismus.service.style.StyleService;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

/**
 * Binds the styles of {@code styles.yml} once the modules have registered their condition types, so a style's
 * requirements may name one, and again after every reload. Before they are bound, and after a reload whose styles
 * cannot be read, what players write is read as it was: plain text before, the previous styles after.
 */
@Singleton
public class DefaultStyleService implements StyleService, EventListener {
	private final Conditions conditions;
	private final Provider<Styles> styles;
	private final Provider<Settings> settings;

	private volatile StyleBook book;

	@Inject
	public DefaultStyleService(
			@NotNull Conditions conditions,
			@NotNull Provider<Styles> styles,
			@NotNull Provider<Settings> settings,
			@NotNull EventManager eventManager
	) {
		this.conditions = conditions;
		this.styles = styles;
		this.settings = settings;

		eventManager.register(this);
	}

	/**
	 * Binds the styles.
	 *
	 * @throws RuntimeException naming the path of a style that cannot be read
	 */
	public synchronized void start() {
		book = bind();
	}

	/**
	 * Closes the styles; what players write is plain text from then on.
	 */
	public synchronized void stop() {
		StyleBook previous = book;
		book = null;
		if (previous != null) previous.close();
	}

	@SocialisticEvent
	public synchronized void onReloaded(PluginReloadedEvent event) {
		StyleBook previous = book;
		if (previous == null) return;

		try {
			book = bind();
			previous.close();
		} catch (RuntimeException exception) {
			Logger.severe("Keeping the styles as they were: %s", exception.getMessage());
		}
	}

	private StyleBook bind() {
		Settings.Serialization serialization = settings.get().getSerialization();

		return StyleBook.bind(conditions, styles.get(), serialization != null && serialization.isEnableLegacyColors());
	}

	@Override
	public @NotNull Optional<String> styleOf(@NotNull SocialismusPlayer writer, @NotNull List<String> styles) {
		StyleBook current = book;
		if (current == null || styles.isEmpty()) return Optional.empty();

		return current.styleOf(writer, styles);
	}

	@Override
	public @NotNull Component read(@NotNull String text, String style) {
		StyleBook current = book;
		if (current == null) return Component.text(text);

		return current.read(text, style);
	}
}
