package me.whereareiam.socialismus.module.dialogue.common.condition;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import me.whereareiam.conditia.Conditions;
import me.whereareiam.conditia.Place;
import me.whereareiam.conditia.exception.ConditionException;
import me.whereareiam.conditia.model.Situation;
import me.whereareiam.conditia.result.Verdict;
import me.whereareiam.socialismus.event.EventListener;
import me.whereareiam.socialismus.event.base.SocialisticEvent;
import me.whereareiam.socialismus.event.plugin.PluginReloadedEvent;
import me.whereareiam.socialismus.exception.StyleException;
import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.module.dialogue.api.model.DialogueFormat;
import me.whereareiam.socialismus.module.dialogue.api.model.config.DialogueSettings;
import me.whereareiam.socialismus.module.dialogue.api.model.config.MessagingFormats;
import me.whereareiam.socialismus.module.dialogue.api.model.config.ModerationFormats;
import me.whereareiam.socialismus.service.Phrasebook;
import me.whereareiam.socialismus.service.style.Styles;
import me.whereareiam.socialismus.service.style.TextStyle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static me.whereareiam.socialismus.model.condition.ConditionParts.RECIPIENT;
import static me.whereareiam.socialismus.model.condition.ConditionParts.SENDER;

/**
 * The conditions of Dialogue's settings and formats: bound when Dialogue is enabled and again after every reload,
 * checked at once. Before they are bound, nobody meets them.
 */
@Singleton
public class DialogueConditions implements EventListener {
	/**
	 * The scope of the phrases Dialogue tells, such as {@code dialogue.denied.sender.permission.missing}.
	 */
	public static final String PHRASE_SCOPE = "dialogue";

	private final Conditions conditions;
	private final Phrasebook phrases;
	private final Styles styles;
	private final Provider<DialogueSettings> settings;
	private final Provider<MessagingFormats> messagingFormats;
	private final Provider<ModerationFormats> moderationFormats;
	private volatile @Nullable DialoguePlaces places;

	@Inject
	public DialogueConditions(
			@NotNull Conditions conditions,
			@NotNull Phrasebook phrases,
			@NotNull Styles styles,
			@NotNull Provider<DialogueSettings> settings,
			@NotNull Provider<MessagingFormats> messagingFormats,
			@NotNull Provider<ModerationFormats> moderationFormats
	) {
		this.conditions = conditions;
		this.phrases = phrases;
		this.styles = styles;
		this.settings = settings;
		this.messagingFormats = messagingFormats;
		this.moderationFormats = moderationFormats;
	}

	/**
	 * Binds the places of the settings and the formats, replacing those bound before.
	 *
	 * @throws ConditionException when a list cannot be bound or refers to a phrase no file has; it names the path,
	 *                            and the previous places stay
	 */
	public synchronized void bind() {
		Map<String, List<DialogueFormat>> formats = new LinkedHashMap<>();
		formats.put("formats.messaging.sent", messagingFormats.get().getSent());
		formats.put("formats.messaging.received", messagingFormats.get().getReceived());
		formats.put("formats.moderation.spy", moderationFormats.get().getSpy());

		DialoguePlaces previous = places;
		places = DialoguePlaces.bind(conditions, phrases, styles, settings.get(), formats);
		if (previous != null) previous.close();
	}

	/**
	 * Closes every place, so none is bound again.
	 */
	public synchronized void close() {
		if (places != null) places.close();

		places = null;
	}

	@SocialisticEvent
	public void onReloaded(PluginReloadedEvent event) {
		try {
			bind();
		} catch (ConditionException | StyleException exception) {
			Logger.severe("Keeping Dialogue's conditions as they were: %s", exception.getMessage());
		}
	}

	/**
	 * Checks whether a sender may write to a recipient: {@code messaging.requires}, then {@code messaging.reaches}. A
	 * recipient on another server cannot be checked here, so it meets only an empty {@code reaches}.
	 *
	 * @param sender    the player who writes
	 * @param recipient the player written to, or {@code null} when they are on another server
	 * @return the verdict; a failed one names the condition and the part it was about
	 */
	public @NotNull Verdict writes(@NotNull SocialismusPlayer sender, @Nullable SocialismusPlayer recipient) {
		DialoguePlaces current = places;
		if (current == null) return unbound("messaging");

		Verdict requires = current.getRequires().check(Situation.of(SENDER, sender));
		if (!requires.passed() || current.isReachesAnyone()) return requires;
		if (recipient == null) return unbound("messaging.reaches");

		return current.getReaches().check(Situation.of(RECIPIENT, recipient).and(SENDER, sender));
	}

	/**
	 * Finds the places of a format.
	 *
	 * @param format the format, from Dialogue's formats as they are loaded now
	 * @return its places; empty before Dialogue is enabled or for a format of an earlier load
	 */
	public @NotNull Optional<FormatPlaces> of(@NotNull DialogueFormat format) {
		DialoguePlaces current = places;

		return current == null ? Optional.empty() : Optional.ofNullable(current.getFormats().get(format));
	}

	/**
	 * Checks whether a sender's messages reach players who turned messages off: {@code control.toggle.bypass}.
	 *
	 * @param sender the sender
	 * @return whether they reach them
	 */
	public boolean bypassesToggle(@NotNull SocialismusPlayer sender) {
		return meets(DialoguePlaces::getBypassesToggle, sender);
	}

	/**
	 * Checks whether a player cannot be ignored: {@code control.ignore.exempt}.
	 *
	 * @param player the player
	 * @return whether nobody ignores them
	 */
	public boolean exemptFromIgnore(@NotNull SocialismusPlayer player) {
		return meets(DialoguePlaces::getExemptFromIgnore, player);
	}

	/**
	 * Checks whether a player's private messages are hidden from spies: {@code moderation.spy.exempt}.
	 *
	 * @param player the player
	 * @return whether spies never see their messages
	 */
	public boolean hiddenFromSpies(@NotNull SocialismusPlayer player) {
		return meets(DialoguePlaces::getHiddenFromSpies, player);
	}

	/**
	 * Chooses the style a sender writes private messages with: the last of {@code messaging.styles} whose conditions
	 * they meet, on the sender's server.
	 *
	 * @param sender the sender
	 * @return the style, {@link TextStyle#PLAIN} when they meet none or Dialogue accepts none
	 */
	public @NotNull TextStyle style(@NotNull SocialismusPlayer sender) {
		DialoguePlaces current = places;

		return current == null ? TextStyle.PLAIN : current.getStyles().of(sender);
	}

	private boolean meets(Function<DialoguePlaces, Place> place, SocialismusPlayer player) {
		DialoguePlaces current = places;

		return current != null && place.apply(current).check(Situation.of(SENDER, player)).passed();
	}

	private static Verdict unbound(String path) {
		return new Verdict.Broken(path, path, new IllegalStateException(path + " cannot be checked here"));
	}

	/**
	 * The places of one format, found through {@link #of}: what the sender and what the recipient must meet.
	 */
	@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
	public static final class FormatPlaces {
		private final Place requires;
		private final Place reaches;
		private final boolean reachesAnyone;

		/**
		 * Checks whether the format fits a private message: its {@code requires} and {@code reaches}. A recipient
		 * on another server cannot be checked here, so it meets only an empty {@code reaches}.
		 *
		 * @param sender    the player who writes
		 * @param recipient the player written to, or {@code null} when they are on another server
		 * @return whether the format fits
		 */
		public boolean fits(@NotNull SocialismusPlayer sender, @Nullable SocialismusPlayer recipient) {
			if (!requires.check(Situation.of(SENDER, sender)).passed()) return false;
			if (reachesAnyone) return true;

			return recipient != null && reaches.check(Situation.of(RECIPIENT, recipient).and(SENDER, sender)).passed();
		}

		void close() {
			requires.close();
			reaches.close();
		}
	}
}
