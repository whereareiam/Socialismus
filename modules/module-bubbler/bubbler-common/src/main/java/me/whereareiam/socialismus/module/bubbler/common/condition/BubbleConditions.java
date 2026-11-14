package me.whereareiam.socialismus.module.bubbler.common.condition;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import me.whereareiam.conditia.Conditions;
import me.whereareiam.conditia.Place;
import me.whereareiam.conditia.exception.ConditionException;
import me.whereareiam.conditia.model.Requirements;
import me.whereareiam.conditia.model.Situation;
import me.whereareiam.socialismus.event.EventListener;
import me.whereareiam.socialismus.event.base.SocialisticEvent;
import me.whereareiam.socialismus.event.plugin.PluginReloadedEvent;
import me.whereareiam.socialismus.exception.StyleException;
import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.Bubble;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.BubbleMessage;
import me.whereareiam.socialismus.service.style.StylePlace;
import me.whereareiam.socialismus.service.style.Styles;
import me.whereareiam.socialismus.service.style.TextStyle;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static me.whereareiam.socialismus.model.condition.ConditionParts.RECIPIENT;
import static me.whereareiam.socialismus.model.condition.ConditionParts.SENDER;
import static me.whereareiam.socialismus.module.bubbler.api.model.condition.BubblerParts.ACTIVATION;

/**
 * The conditions and styles of the bubbles: bound when Bubbler is enabled and again after every reload, and handed
 * out per bubble as its {@link Places}, which check them at once on the thread that shows the bubble.
 */
@Singleton
public class BubbleConditions implements EventListener {
	private final Conditions conditions;
	private final Styles styles;
	private final Provider<List<Bubble>> bubbles;
	private volatile Map<String, Places> places = Map.of();

	@Inject
	public BubbleConditions(@NotNull Conditions conditions, @NotNull Styles styles, @NotNull Provider<List<Bubble>> bubbles) {
		this.conditions = conditions;
		this.styles = styles;
		this.bubbles = bubbles;
	}

	/**
	 * Binds the conditions of every bubble, replacing those bound before.
	 *
	 * @throws ConditionException when a list cannot be bound; it names the list's path, and the previous places stay
	 */
	public synchronized void bind() {
		Map<String, Places> bound = new HashMap<>();
		try {
			for (Bubble bubble : bubbles.get()) bound.put(bubble.getId(), Places.bind(conditions, styles, bubble));
		} catch (ConditionException | StyleException exception) {
			bound.values().forEach(Places::close);
			throw exception;
		}

		Map<String, Places> previous = places;
		places = Map.copyOf(bound);
		previous.values().forEach(Places::close);
	}

	/**
	 * Closes every place, so none is bound again.
	 */
	public synchronized void close() {
		places.values().forEach(Places::close);
		places = Map.of();
	}

	@SocialisticEvent
	public void onReloaded(PluginReloadedEvent event) {
		try {
			bind();
		} catch (ConditionException | StyleException exception) {
			Logger.severe("Keeping the conditions of the bubbles as they were: %s", exception.getMessage());
		}
	}

	/**
	 * Finds the places of a bubble.
	 *
	 * @param bubble the bubble
	 * @return its places; empty when it has none, before Bubbler is enabled or for a bubble Bubbler does not hold
	 */
	public @NotNull Optional<Places> of(@NotNull Bubble bubble) {
		return Optional.ofNullable(places.get(bubble.getId()));
	}

	/**
	 * The bound places of one bubble, bound together and closed together when the bubbles are reloaded: whose
	 * messages raise it, who sees it, and the style its sender writes with.
	 */
	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	public static final class Places {
		private final Place requires;
		private final Place reaches;
		private final StylePlace styles;

		private static Places bind(Conditions conditions, Styles styles, Bubble bubble) {
			String path = "bubbles." + bubble.getId();
			Place requires = conditions.place(path + ".requires", listOf(bubble.getRequires()), SENDER, ACTIVATION);
			Place reaches = null;
			try {
				reaches = conditions.place(path + ".reaches", listOf(bubble.getReaches()), RECIPIENT, SENDER, ACTIVATION);
				List<String> accepted = bubble.getStyles() == null ? List.of() : bubble.getStyles();

				return new Places(requires, reaches, styles.place(path + ".styles", accepted));
			} catch (ConditionException | StyleException exception) {
				requires.close();
				if (reaches != null) reaches.close();
				throw exception;
			}
		}

		private static Requirements listOf(Requirements requires) {
			return requires == null ? Requirements.none() : requires;
		}

		/**
		 * Checks whether a sender gets the bubble: its {@code requires}.
		 *
		 * @param message the bubble message, with its sender
		 * @return whether the sender meets the bubble's conditions
		 */
		public boolean selects(@NotNull BubbleMessage message) {
			return requires.check(Situation.of(SENDER, message.getSender()).and(ACTIVATION, message)).passed();
		}

		/**
		 * Checks whether a nearby player sees the bubble: its {@code reaches}.
		 *
		 * @param recipient the nearby player
		 * @param message   the bubble message, with its sender
		 * @return whether the player sees the bubble
		 */
		public boolean reaches(@NotNull SocialismusPlayer recipient, @NotNull BubbleMessage message) {
			Situation situation = Situation.of(RECIPIENT, recipient).and(SENDER, message.getSender()).and(ACTIVATION, message);

			return reaches.check(situation).passed();
		}

		/**
		 * Chooses the style a sender writes the bubble with: the last of its {@code styles} whose conditions they
		 * meet.
		 *
		 * @param sender the sender
		 * @return the style, {@link TextStyle#PLAIN} when they meet none or the bubble accepts none
		 */
		public @NotNull TextStyle style(@NotNull SocialismusPlayer sender) {
			return styles.of(sender);
		}

		private void close() {
			requires.close();
			reaches.close();
			styles.close();
		}
	}
}
