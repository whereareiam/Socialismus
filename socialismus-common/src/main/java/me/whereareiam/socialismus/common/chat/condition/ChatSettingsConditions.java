package me.whereareiam.socialismus.common.chat.condition;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import me.whereareiam.conditia.Conditions;
import me.whereareiam.conditia.Place;
import me.whereareiam.conditia.exception.ConditionException;
import me.whereareiam.conditia.model.Situation;
import me.whereareiam.socialismus.event.EventListener;
import me.whereareiam.socialismus.event.EventManager;
import me.whereareiam.socialismus.event.base.SocialisticEvent;
import me.whereareiam.socialismus.event.plugin.PluginReloadedEvent;
import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.model.chat.ChatSettings;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.service.chat.ChatHistoryConditions;
import me.whereareiam.socialismus.service.chat.ChatToggleConditions;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

import static me.whereareiam.socialismus.model.condition.ConditionParts.SENDER;
import static me.whereareiam.socialismus.model.condition.ConditionParts.VIEWER;

/**
 * Binds the conditions of {@code chat/settings.yml} in the conditions' phase and again after every reload, all of
 * them together: when one of the file's lists cannot be bound, every list stays as it was.
 */
@Singleton
public class ChatSettingsConditions implements ChatHistoryConditions, ChatToggleConditions, EventListener {
	private final Conditions conditions;
	private final Provider<ChatSettings> chatSettings;
	private volatile SettingsPlaces settings;

	@Inject
	public ChatSettingsConditions(
			@NotNull Conditions conditions,
			@NotNull Provider<ChatSettings> chatSettings,
			@NotNull EventManager eventManager
	) {
		this.conditions = conditions;
		this.chatSettings = chatSettings;

		eventManager.register(this);
	}

	/**
	 * Binds the lists of the chat settings.
	 *
	 * @throws ConditionException when a list cannot be bound; it names the list's path
	 */
	public synchronized void start() {
		settings = SettingsPlaces.bind(conditions, chatSettings.get());
	}

	/**
	 * Closes the places, so none is bound again.
	 */
	public synchronized void stop() {
		if (settings != null) settings.close();

		settings = null;
	}

	@SocialisticEvent
	public synchronized void onReloaded(PluginReloadedEvent event) {
		SettingsPlaces previous = settings;
		if (previous == null) return;

		try {
			settings = SettingsPlaces.bind(conditions, chatSettings.get());
			previous.close();
		} catch (ConditionException exception) {
			Logger.severe("Keeping the conditions of the chat settings as they were: %s", exception.getMessage());
		}
	}

	@Override
	public boolean clears(@NotNull SocialismusPlayer viewer) {
		return meets(SettingsPlaces::getClears, Situation.of(VIEWER, viewer));
	}

	@Override
	public boolean keeps(@NotNull SocialismusPlayer sender) {
		return meets(SettingsPlaces::getKeeps, Situation.of(SENDER, sender));
	}

	@Override
	public boolean bypasses(@NotNull SocialismusPlayer sender) {
		return meets(SettingsPlaces::getBypasses, Situation.of(SENDER, sender));
	}

	private boolean meets(Function<SettingsPlaces, Place> place, Situation situation) {
		SettingsPlaces current = settings;

		return current != null && place.apply(current).check(situation).passed();
	}
}
