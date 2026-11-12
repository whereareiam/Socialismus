package me.whereareiam.socialismus.module.chirper.common.requirement;

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
import me.whereareiam.socialismus.module.chirper.api.model.announcement.Announcement;
import me.whereareiam.socialismus.module.chirper.api.model.announcement.AnnouncementContent;
import me.whereareiam.socialismus.module.chirper.api.type.AnnouncementType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.whereareiam.socialismus.model.requirement.RequirementParts.RECIPIENT;

/**
 * The requirements of the announcements and of their contents, about the player who would receive them: bound when
 * Chirper is enabled and again after every reload. An announcement that is not bound reaches nobody.
 */
@Singleton
public class AnnouncementRequirements implements EventListener {
	private final Conditions conditions;
	private final Provider<List<Announcement>> announcements;
	private volatile Map<String, Places> places = Map.of();

	@Inject
	public AnnouncementRequirements(@NotNull Conditions conditions, @NotNull Provider<List<Announcement>> announcements) {
		this.conditions = conditions;
		this.announcements = announcements;
	}

	/**
	 * Binds the requirements of every announcement, replacing those bound before.
	 *
	 * @throws ConditionException when a list cannot be bound; it names the list's path, and the previous places stay
	 */
	public synchronized void bind() {
		Map<String, Places> bound = new HashMap<>();
		try {
			for (Announcement announcement : announcements.get()) bound.put(announcement.getId(), Places.bind(conditions, announcement));
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
			Logger.severe("Keeping the requirements of the announcements as they were: %s", exception.getMessage());
		}
	}

	/**
	 * Checks whether an announcement reaches a player: its {@code requires}.
	 *
	 * @param announcement the announcement
	 * @param recipient    the player
	 * @return whether the player receives it
	 */
	public boolean reaches(@NotNull Announcement announcement, @NotNull SocialismusPlayer recipient) {
		Places bound = places.get(announcement.getId());

		return bound != null && bound.announcement().check(Situation.of(RECIPIENT, recipient)).passed();
	}

	/**
	 * Checks whether one content of an announcement reaches a player: the content's {@code requires}.
	 *
	 * @param announcement the announcement
	 * @param type         the content's type
	 * @param recipient    the player
	 * @return whether the player receives the content
	 */
	public boolean reaches(@NotNull Announcement announcement, @NotNull AnnouncementType type, @NotNull SocialismusPlayer recipient) {
		Places bound = places.get(announcement.getId());
		Place content = bound == null ? null : bound.contents().get(type);

		return content != null && content.check(Situation.of(RECIPIENT, recipient)).passed();
	}

	private record Places(Place announcement, Map<AnnouncementType, Place> contents) {
		static Places bind(Conditions conditions, Announcement announcement) {
			String path = "announcements." + announcement.getId();
			List<Place> bound = new ArrayList<>();
			try {
				Place own = open(bound, conditions.place(path + ".requires", listOf(announcement.getRequires()), RECIPIENT));
				Map<AnnouncementType, Place> contents = new EnumMap<>(AnnouncementType.class);
				Map<AnnouncementType, ? extends AnnouncementContent> written = announcement.getContents() == null ? Map.of() : announcement.getContents();
				written.forEach((type, content) -> contents.put(type,
						open(bound, conditions.place(path + ".contents." + type + ".requires", listOf(content.getRequires()), RECIPIENT))));

				return new Places(own, contents);
			} catch (ConditionException exception) {
				bound.forEach(Place::close);
				throw exception;
			}
		}

		private static Place open(List<Place> bound, Place place) {
			bound.add(place);

			return place;
		}

		private static Requirements listOf(Requirements requires) {
			return requires == null ? Requirements.none() : requires;
		}

		void close() {
			announcement.close();
			contents.values().forEach(Place::close);
		}
	}
}
