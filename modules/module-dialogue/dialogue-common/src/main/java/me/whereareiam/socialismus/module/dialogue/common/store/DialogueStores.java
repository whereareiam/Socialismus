package me.whereareiam.socialismus.module.dialogue.common.store;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.module.dialogue.api.ConversationStore;
import me.whereareiam.socialismus.module.dialogue.api.IgnoreStore;
import me.whereareiam.socialismus.module.dialogue.api.PreferencesStore;
import org.jetbrains.annotations.NotNull;

/**
 * The stores Dialogue keeps its data in: its own in memory, until a module offers others, such as SocialismusNetwork's
 * replicated ones, and again after that module goes away. Dialogue reaches its stores only through the
 * {@code Selected...} stores, which always use the ones chosen here.
 */
@Singleton
public class DialogueStores {
	private final PreferencesStore memoryPreferences;
	private final IgnoreStore memoryIgnores;
	private final ConversationStore memoryConversations;

	private volatile PreferencesStore preferences;
	private volatile IgnoreStore ignores;
	private volatile ConversationStore conversations;

	@Inject
	public DialogueStores(
			@NotNull InMemoryPreferencesStore preferences,
			@NotNull InMemoryIgnoreStore ignores,
			@NotNull InMemoryConversationStore conversations
	) {
		this.memoryPreferences = preferences;
		this.memoryIgnores = ignores;
		this.memoryConversations = conversations;
		reset();
	}

	/**
	 * Uses the given stores from now on.
	 */
	public void use(@NotNull PreferencesStore preferences, @NotNull IgnoreStore ignores, @NotNull ConversationStore conversations) {
		this.preferences = preferences;
		this.ignores = ignores;
		this.conversations = conversations;
	}

	/**
	 * Goes back to the stores in memory.
	 */
	public void reset() {
		use(memoryPreferences, memoryIgnores, memoryConversations);
	}

	@NotNull PreferencesStore preferences() {
		return preferences;
	}

	@NotNull IgnoreStore ignores() {
		return ignores;
	}

	@NotNull ConversationStore conversations() {
		return conversations;
	}
}
