package me.whereareiam.socialismus.common.chat;

import com.google.inject.Inject;
import com.google.inject.Injector;
import me.whereareiam.socialismus.common.chat.worker.base.ChatSelector;
import me.whereareiam.socialismus.common.chat.worker.base.RecipientResolver;
import me.whereareiam.socialismus.common.chat.worker.base.RecipientSelector;
import me.whereareiam.socialismus.common.chat.worker.formatted.FormatSelector;
import me.whereareiam.socialismus.common.container.ChatContainer;
import me.whereareiam.socialismus.lifecycle.RuntimeLifecycle;
import me.whereareiam.socialismus.lifecycle.StartupPhase;
import org.jetbrains.annotations.NotNull;

/**
 * Starts the chats: loads them from the chats document and adds the workers of the core to the chat pipeline, which
 * they join when they are built, so the first message finds them in place.
 */
public final class ChatLifecycle implements RuntimeLifecycle {
	private final Injector injector;
	private final ChatContainer chatContainer;

	@Inject
	public ChatLifecycle(@NotNull Injector injector, @NotNull ChatContainer chatContainer) {
		this.injector = injector;
		this.chatContainer = chatContainer;
	}

	@Override
	public @NotNull StartupPhase phase() {
		return StartupPhase.RUNTIME;
	}

	@Override
	public void initialize() {
		chatContainer.load();

		injector.getInstance(RecipientResolver.class);
		injector.getInstance(ChatSelector.class);
		injector.getInstance(RecipientSelector.class);
		injector.getInstance(FormatSelector.class);
	}

	/**
	 * Leaves the chats and the workers in place: nothing reaches the pipeline once the listeners are gone.
	 */
	@Override
	public void shutdown() {
	}
}
