package me.whereareiam.socialismus.module.mentionator.common.condition;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import me.whereareiam.conditia.Conditions;
import me.whereareiam.conditia.exception.ConditionException;
import me.whereareiam.socialismus.event.EventListener;
import me.whereareiam.socialismus.event.base.SocialisticEvent;
import me.whereareiam.socialismus.event.plugin.PluginReloadedEvent;
import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.module.mentionator.api.model.config.MentionatorSettings;
import me.whereareiam.socialismus.module.mentionator.api.model.notification.Notification;
import me.whereareiam.socialismus.module.mentionator.api.model.tag.Tag;
import me.whereareiam.socialismus.service.Phrasebook;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Mentionator's tags, notifications and limits with their lists bound: bound when Mentionator is enabled and again
 * after every reload, checked at once. Before they are bound, there is no tag.
 */
@Singleton
public class MentionatorConditions implements EventListener {
	/**
	 * The scope of the phrases Mentionator tells, such as {@code mentionator.denied.permission.missing}.
	 */
	public static final String PHRASE_SCOPE = "mentionator";

	private final Conditions conditions;
	private final Phrasebook phrases;
	private final Provider<List<Tag>> tags;
	private final Provider<List<Notification>> notifications;
	private final Provider<MentionatorSettings> settings;
	private volatile Catalog catalog = Catalog.empty();

	@Inject
	public MentionatorConditions(
			@NotNull Conditions conditions,
			@NotNull Phrasebook phrases,
			@NotNull Provider<List<Tag>> tags,
			@NotNull Provider<List<Notification>> notifications,
			@NotNull Provider<MentionatorSettings> settings
	) {
		this.conditions = conditions;
		this.phrases = phrases;
		this.tags = tags;
		this.notifications = notifications;
		this.settings = settings;
	}

	/**
	 * Returns the tags and notifications as they are bound now.
	 *
	 * @return the catalog, empty before Mentionator is enabled
	 */
	public @NotNull Catalog catalog() {
		return catalog;
	}

	/**
	 * Binds the lists of the tags, notifications and settings, replacing those bound before.
	 *
	 * @throws ConditionException when a list cannot be bound or refers to a phrase no file has; it names the path,
	 *                            and the previous lists stay
	 */
	public synchronized void bind() {
		Catalog previous = catalog;
		catalog = Catalog.bind(conditions, phrases, tags.get(), notifications.get(), settings.get());
		previous.close();
	}

	/**
	 * Closes every list, so no tag is found until they are bound again.
	 */
	public synchronized void close() {
		catalog.close();
		catalog = Catalog.empty();
	}

	@SocialisticEvent
	public void onReloaded(PluginReloadedEvent event) {
		try {
			bind();
		} catch (ConditionException exception) {
			Logger.severe("Keeping Mentionator's tags as they were: %s", exception.getMessage());
		}
	}
}
