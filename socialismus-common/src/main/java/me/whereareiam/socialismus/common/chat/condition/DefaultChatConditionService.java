package me.whereareiam.socialismus.common.chat.condition;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import me.whereareiam.conditia.Conditions;
import me.whereareiam.conditia.Place;
import me.whereareiam.conditia.exception.ConditionException;
import me.whereareiam.conditia.model.Situation;
import me.whereareiam.conditia.result.Verdict;
import me.whereareiam.socialismus.event.EventListener;
import me.whereareiam.socialismus.event.EventManager;
import me.whereareiam.socialismus.event.base.SocialisticEvent;
import me.whereareiam.socialismus.event.plugin.PluginReloadedEvent;
import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.model.chat.Chat;
import me.whereareiam.socialismus.model.chat.ChatFormat;
import me.whereareiam.socialismus.model.chat.ChatSettings;
import me.whereareiam.socialismus.model.chat.message.ChatMessage;
import me.whereareiam.socialismus.model.condition.Recordings;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.service.chat.ChatConditionService;
import me.whereareiam.socialismus.service.phrase.PhraseScope;
import me.whereareiam.socialismus.service.phrase.Phrasebook;
import me.whereareiam.socialismus.service.style.Styles;
import me.whereareiam.socialismus.service.style.TextStyle;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import static me.whereareiam.socialismus.model.condition.ConditionParts.MESSAGE;
import static me.whereareiam.socialismus.model.condition.ConditionParts.RECIPIENT;
import static me.whereareiam.socialismus.model.condition.ConditionParts.SENDER;
import static me.whereareiam.socialismus.model.condition.ConditionParts.VIEWER;

/**
 * Binds the conditions of the chats and of the chat settings, and checks them. Chats held before the conditions'
 * phase wait until then, so their lists may name the condition types of modules; afterwards a chat is bound as it is
 * added, and the settings again after every reload.
 */
@Singleton
public class DefaultChatConditionService implements ChatConditionService, EventListener {
	private static final String PHRASE_SCOPE = "chat";

	private final Conditions conditions;
	private final PhraseScope phrases;
	private final Styles styles;
	private final Provider<ChatSettings> chatSettings;

	private final Map<String, ChatPlaces> chats = new ConcurrentHashMap<>();
	private final Map<String, Chat> waiting = new ConcurrentHashMap<>();
	private volatile SettingsPlaces settings;
	private volatile boolean started;

	@Inject
	public DefaultChatConditionService(
			@NotNull Conditions conditions,
			@NotNull Phrasebook phrases,
			@NotNull Styles styles,
			@NotNull Provider<ChatSettings> chatSettings,
			@NotNull EventManager eventManager
	) {
		this.conditions = conditions;
		this.phrases = phrases.scope(PHRASE_SCOPE);
		this.styles = styles;
		this.chatSettings = chatSettings;

		eventManager.register(this);
	}

	/**
	 * Binds the settings and the chats that waited for the modules.
	 *
	 * @throws ConditionException when a list cannot be bound; it names the list's path
	 */
	public synchronized void start() {
		settings = SettingsPlaces.bind(conditions, chatSettings.get());
		for (Chat chat : waiting.values()) replace(chat.getId(), ChatPlaces.bind(conditions, phrases, styles, chat));

		waiting.clear();
		started = true;
	}

	/**
	 * Closes every place, so none is bound again.
	 */
	public synchronized void stop() {
		started = false;
		forgetChats();
		if (settings != null) settings.close();

		settings = null;
	}

	/**
	 * Binds the places of a chat, replacing those of a chat with its id. Before the conditions' phase the chat waits.
	 *
	 * @param chat the chat
	 * @throws ConditionException when one of its lists cannot be bound; the chat keeps no places then
	 */
	public synchronized void bind(@NotNull Chat chat) {
		if (!started) {
			waiting.put(chat.getId(), chat);
			return;
		}

		replace(chat.getId(), ChatPlaces.bind(conditions, phrases, styles, chat));
	}

	/**
	 * Closes the places of every chat, before the chats are loaded again.
	 */
	public synchronized void forgetChats() {
		waiting.clear();
		chats.values().forEach(ChatPlaces::close);
		chats.clear();
	}

	@Override
	public @NotNull PhraseScope phrases() {
		return phrases;
	}

	@SocialisticEvent
	public synchronized void onReloaded(PluginReloadedEvent event) {
		if (!started) return;

		try {
			SettingsPlaces previous = settings;
			settings = SettingsPlaces.bind(conditions, chatSettings.get());
			previous.close();
		} catch (ConditionException exception) {
			Logger.severe("Keeping the conditions of the chat settings as they were: %s", exception.getMessage());
		}
	}

	@Override
	public @NotNull Verdict writes(@NotNull Chat chat, @NotNull ChatMessage message) {
		Optional<ChatPlaces> places = Optional.ofNullable(chats.get(chat.getId()));
		if (places.isEmpty()) return unbound("chats." + chat.getId());

		return places.get().getRequires().check(Situation.of(SENDER, message.getSender()).and(MESSAGE, message));
	}

	@Override
	public @NotNull Verdict reaches(@NotNull Chat chat, @NotNull SocialismusPlayer recipient, @NotNull ChatMessage message) {
		Optional<ChatPlaces> places = Optional.ofNullable(chats.get(chat.getId()));
		if (places.isEmpty()) return unbound("chats." + chat.getId());

		return places.get().getReaches().check(Situation.of(RECIPIENT, recipient)
				.and(SENDER, message.getSender())
				.and(MESSAGE, message));
	}

	@Override
	public @NotNull Optional<ChatFormat> format(@NotNull Chat chat, @NotNull ChatMessage message) {
		ChatPlaces places = chats.get(chat.getId());
		List<ChatFormat> formats = chat.getFormats();
		if (places == null || formats == null || formats.size() != places.getFormats().size()) return Optional.empty();

		Situation situation = Situation.of(SENDER, message.getSender()).and(MESSAGE, message);
		for (int index = formats.size() - 1; index >= 0; index--) {
			Verdict verdict = places.getFormats().get(index).check(situation);
			if (!verdict.passed()) continue;

			Recordings.of(message).add(verdict);
			return Optional.of(formats.get(index));
		}

		return Optional.empty();
	}

	@Override
	public @NotNull TextStyle style(@NotNull Chat chat, @NotNull SocialismusPlayer sender) {
		ChatPlaces places = chats.get(chat.getId());

		return places == null ? TextStyle.PLAIN : places.getStyles().of(sender);
	}

	@Override
	public boolean clears(@NotNull SocialismusPlayer viewer) {
		return meets(SettingsPlaces::getClears, Situation.of(VIEWER, viewer));
	}

	@Override
	public boolean keepsHistory(@NotNull SocialismusPlayer sender) {
		return meets(SettingsPlaces::getKeepsHistory, Situation.of(SENDER, sender));
	}

	@Override
	public boolean bypassesToggle(@NotNull SocialismusPlayer sender) {
		return meets(SettingsPlaces::getBypassToggle, Situation.of(SENDER, sender));
	}

	private boolean meets(Function<SettingsPlaces, Place> place, Situation situation) {
		SettingsPlaces current = settings;

		return current != null && place.apply(current).check(situation).passed();
	}

	private void replace(String id, ChatPlaces places) {
		ChatPlaces previous = chats.put(id, places);
		if (previous != null) previous.close();
	}

	private static Verdict unbound(String path) {
		return new Verdict.Broken(path, path, new IllegalStateException("The conditions of " + path + " are not bound"));
	}
}
