package me.whereareiam.socialismus.command.suggestion;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.model.chat.Chat;
import me.whereareiam.socialismus.service.container.ChatContainerService;
import org.incendo.cloud.annotations.suggestion.Suggestions;

import java.util.Collection;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public final class ChatSuggestionProvider {
	private final ChatContainerService chats;

	/**
	 * Suggests the chats players may turn off.
	 */
	@Suggestions("toggleableChats")
	public Collection<String> suggestToggleableChats() {
		return chats.getChats().stream()
				.filter(Chat::isToggleable)
				.map(Chat::getId)
				.sorted()
				.toList();
	}
}
