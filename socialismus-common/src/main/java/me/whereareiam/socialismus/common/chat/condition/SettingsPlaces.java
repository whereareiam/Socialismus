package me.whereareiam.socialismus.common.chat.condition;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.whereareiam.conditia.Conditions;
import me.whereareiam.conditia.Place;
import me.whereareiam.conditia.model.Requirements;
import me.whereareiam.socialismus.model.chat.ChatSettings;

import java.util.ArrayList;
import java.util.List;

import static me.whereareiam.socialismus.model.condition.ConditionParts.SENDER;
import static me.whereareiam.socialismus.model.condition.ConditionParts.VIEWER;

/**
 * The places of {@code chat/settings.yml}, bound together and closed together when the file is reloaded:
 * {@code history.requires} with the part viewer, {@code history.bypass} and {@code toggle.bypass} with the part
 * sender.
 */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
final class SettingsPlaces implements AutoCloseable {
	private final Place clears;
	private final Place keepsHistory;
	private final Place bypassToggle;

	/**
	 * Binds the places of the chat settings.
	 *
	 * @param conditions binds the places
	 * @param settings   the chat settings
	 * @return the places
	 * @throws me.whereareiam.conditia.exception.ConditionException when one of the lists cannot be bound; the places
	 *                                                              bound before are closed
	 */
	static SettingsPlaces bind(Conditions conditions, ChatSettings settings) {
		ChatSettings.ChatHistorySettings history = settings.getHistory();
		ChatSettings.Toggle toggle = settings.getToggle();
		List<Place> bound = new ArrayList<>();
		try {
			Place clears = open(bound, conditions.place("chat.settings.history.requires", history == null ? none() : ChatPlaces.listOf(history.getRequires()), VIEWER));
			Place keeps = open(bound, conditions.place("chat.settings.history.bypass", history == null ? none() : ChatPlaces.listOf(history.getBypass()), SENDER));
			Place bypass = open(bound, conditions.place("chat.settings.toggle.bypass", toggle == null ? none() : ChatPlaces.listOf(toggle.getBypass()), SENDER));

			return new SettingsPlaces(clears, keeps, bypass);
		} catch (RuntimeException failure) {
			bound.forEach(Place::close);
			throw failure;
		}
	}

	private static Requirements none() {
		return Requirements.none();
	}

	private static Place open(List<Place> bound, Place place) {
		bound.add(place);

		return place;
	}

	@Override
	public void close() {
		clears.close();
		keepsHistory.close();
		bypassToggle.close();
	}
}
