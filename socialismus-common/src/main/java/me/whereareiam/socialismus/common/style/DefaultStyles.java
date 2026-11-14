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
import me.whereareiam.socialismus.model.style.StylesConfig;
import me.whereareiam.socialismus.service.style.StylePlace;
import me.whereareiam.socialismus.service.style.Styles;
import me.whereareiam.socialismus.service.style.TextStyle;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Binds the styles of {@code styles.yml} once the modules have registered their condition types, so a style's
 * conditions may name one, and again after every reload; and keeps the places that accept them, which it checks
 * against every generation of styles before using it. Before the styles are bound, everyone writes plain text; a
 * reload whose styles or places cannot be read keeps the previous styles.
 */
@Singleton
public class DefaultStyles implements Styles, EventListener {
	private final Conditions conditions;
	private final Provider<StylesConfig> file;
	private final Provider<Settings> settings;

	private final Set<OpenPlace> places = ConcurrentHashMap.newKeySet();
	private volatile StyleBook book;

	@Inject
	public DefaultStyles(
			@NotNull Conditions conditions,
			@NotNull Provider<StylesConfig> file,
			@NotNull Provider<Settings> settings,
			@NotNull EventManager eventManager
	) {
		this.conditions = conditions;
		this.file = file;
		this.settings = settings;

		eventManager.register(this);
	}

	/**
	 * Binds the styles and checks the places bound before them.
	 *
	 * @throws me.whereareiam.socialismus.exception.StyleException naming the path of a style or a place that cannot be
	 *                                                             read
	 */
	public synchronized void start() {
		StyleBook next = bind();
		try {
			places.forEach(place -> next.check(place.path, place.accepted));
		} catch (RuntimeException failure) {
			next.close();
			throw failure;
		}

		book = next;
	}

	/**
	 * Closes the styles; everyone writes plain text from then on.
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
			start();
			previous.close();
		} catch (RuntimeException exception) {
			Logger.severe("Keeping the styles as they were: %s", exception.getMessage());
		}
	}

	@Override
	public synchronized @NotNull StylePlace place(@NotNull String path, @NotNull List<String> accepted) {
		OpenPlace place = new OpenPlace(path, List.copyOf(accepted));
		if (book != null) book.check(path, place.accepted);

		places.add(place);

		return place;
	}

	@Override
	public @NotNull TextStyle style(@NotNull String id) {
		StyleBook current = book;

		return current == null ? TextStyle.PLAIN : current.style(id);
	}

	private StyleBook bind() {
		Settings.Serialization serialization = settings.get().getSerialization();

		return StyleBook.bind(conditions, file.get(), serialization != null && serialization.isEnableLegacyColors());
	}

	private final class OpenPlace implements StylePlace {
		private final String path;
		private final List<String> accepted;

		private OpenPlace(String path, List<String> accepted) {
			this.path = path;
			this.accepted = accepted;
		}

		@Override
		public @NotNull TextStyle of(@NotNull SocialismusPlayer writer) {
			StyleBook current = book;
			if (current == null || accepted.isEmpty()) return TextStyle.PLAIN;

			return current.styleOf(writer, accepted);
		}

		@Override
		public void close() {
			places.remove(this);
		}
	}
}
