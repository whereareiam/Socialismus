package me.whereareiam.socialismus.common.chat.condition;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.conditia.Conditions;
import me.whereareiam.conditia.exception.ConditionException;
import me.whereareiam.socialismus.model.chat.Chat;
import me.whereareiam.socialismus.service.Phrasebook;
import me.whereareiam.socialismus.service.chat.ChatConditions;
import me.whereareiam.socialismus.service.chat.ChatPlaces;
import me.whereareiam.socialismus.service.style.Styles;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Binds the places of the chats. Chats held before the conditions' phase wait until then, so their lists may name
 * the condition types of modules; afterwards a chat is bound as it is added.
 */
@Singleton
public class DefaultChatConditions implements ChatConditions {
	private final Conditions conditions;
	private final Phrasebook phrases;
	private final Styles styles;

	private final Map<String, BoundChatPlaces> chats = new ConcurrentHashMap<>();
	private final Map<String, Chat> waiting = new ConcurrentHashMap<>();
	private volatile boolean started;

	@Inject
	public DefaultChatConditions(@NotNull Conditions conditions, @NotNull Phrasebook phrases, @NotNull Styles styles) {
		this.conditions = conditions;
		this.phrases = phrases;
		this.styles = styles;
	}

	/**
	 * Binds the chats that waited for the modules.
	 *
	 * @throws ConditionException when a list cannot be bound; it names the list's path
	 */
	public synchronized void start() {
		for (Chat chat : waiting.values()) replace(chat.getId(), BoundChatPlaces.bind(conditions, phrases, styles, chat));

		waiting.clear();
		started = true;
	}

	/**
	 * Closes every place, so none is bound again.
	 */
	public synchronized void stop() {
		started = false;
		forgetChats();
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

		replace(chat.getId(), BoundChatPlaces.bind(conditions, phrases, styles, chat));
	}

	/**
	 * Closes the places of every chat, before the chats are loaded again.
	 */
	public synchronized void forgetChats() {
		waiting.clear();
		chats.values().forEach(BoundChatPlaces::close);
		chats.clear();
	}

	@Override
	public @NotNull Optional<ChatPlaces> of(@NotNull Chat chat) {
		return Optional.ofNullable(chats.get(chat.getId()));
	}

	private void replace(String id, BoundChatPlaces places) {
		BoundChatPlaces previous = chats.put(id, places);
		if (previous != null) previous.close();
	}
}
