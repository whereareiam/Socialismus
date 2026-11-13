package me.whereareiam.socialismus.module.bubbler.common.condition;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import me.whereareiam.conditia.Conditions;
import me.whereareiam.conditia.Place;
import me.whereareiam.conditia.exception.ConditionException;
import me.whereareiam.conditia.model.Requirements;
import me.whereareiam.conditia.model.Situation;
import me.whereareiam.socialismus.event.EventListener;
import me.whereareiam.socialismus.event.base.SocialisticEvent;
import me.whereareiam.socialismus.event.plugin.PluginReloadedEvent;
import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.Bubble;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.BubbleMessage;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.whereareiam.socialismus.model.condition.ConditionParts.RECIPIENT;
import static me.whereareiam.socialismus.model.condition.ConditionParts.SENDER;
import static me.whereareiam.socialismus.module.bubbler.api.model.condition.BubblerParts.ACTIVATION;

/**
 * The conditions of the bubbles: bound when Bubbler is enabled and again after every reload, checked at once on
 * the thread that shows the bubble. A bubble that is not bound meets nobody.
 */
@Singleton
public class BubbleConditions implements EventListener {
	private final Conditions conditions;
	private final Provider<List<Bubble>> bubbles;
	private volatile Map<String, Places> places = Map.of();

	@Inject
	public BubbleConditions(@NotNull Conditions conditions, @NotNull Provider<List<Bubble>> bubbles) {
		this.conditions = conditions;
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
			for (Bubble bubble : bubbles.get()) bound.put(bubble.getId(), Places.bind(conditions, bubble));
		} catch (ConditionException exception) {
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
		} catch (ConditionException exception) {
			Logger.severe("Keeping the conditions of the bubbles as they were: %s", exception.getMessage());
		}
	}

	/**
	 * Checks whether a sender gets a bubble: its {@code requires}.
	 *
	 * @param bubble  the bubble
	 * @param message the bubble message, with its sender
	 * @return whether the sender meets the bubble's conditions
	 */
	public boolean selects(@NotNull Bubble bubble, @NotNull BubbleMessage message) {
		Places bound = places.get(bubble.getId());

		return bound != null && bound.requires().check(Situation.of(SENDER, message.getSender()).and(ACTIVATION, message)).passed();
	}

	/**
	 * Checks whether a nearby player sees a bubble: its {@code reaches}.
	 *
	 * @param bubble    the bubble
	 * @param recipient the nearby player
	 * @param message   the bubble message, with its sender
	 * @return whether the player sees the bubble
	 */
	public boolean reaches(@NotNull Bubble bubble, @NotNull SocialismusPlayer recipient, @NotNull BubbleMessage message) {
		Places bound = places.get(bubble.getId());
		Situation situation = Situation.of(RECIPIENT, recipient).and(SENDER, message.getSender()).and(ACTIVATION, message);

		return bound != null && bound.reaches().check(situation).passed();
	}

	private record Places(Place requires, Place reaches) {
		static Places bind(Conditions conditions, Bubble bubble) {
			String path = "bubbles." + bubble.getId();
			Place requires = conditions.place(path + ".requires", listOf(bubble.getRequires()), SENDER, ACTIVATION);
			try {
				return new Places(requires, conditions.place(path + ".reaches", listOf(bubble.getReaches()), RECIPIENT, SENDER, ACTIVATION));
			} catch (ConditionException exception) {
				requires.close();
				throw exception;
			}
		}

		private static Requirements listOf(Requirements requires) {
			return requires == null ? Requirements.none() : requires;
		}

		void close() {
			requires.close();
			reaches.close();
		}
	}
}
