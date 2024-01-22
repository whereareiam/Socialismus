package me.whereareiam.socialismus.core.module.swapper;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.model.BubbleMessage;
import me.whereareiam.socialismus.api.model.chat.ChatMessage;
import org.bukkit.entity.Player;

@Singleton
public class SwapperService {
	private final SwapperFormatter swapperFormatter;
	private final SwapperSuggester swapperSuggester;

	@Inject
	public SwapperService(SwapperFormatter swapperFormatter, SwapperSuggester swapperSuggester) {
		this.swapperFormatter = swapperFormatter;
		this.swapperSuggester = swapperSuggester;
	}

	public BubbleMessage hookSwapper(BubbleMessage bubbleMessage) {
		return swapperFormatter.hookSwapper(bubbleMessage);
	}

	public ChatMessage hookSwapper(ChatMessage chatMessage) {
		return swapperFormatter.hookSwapper(chatMessage);
	}

	public void suggestSwappers(Player player) {
		swapperSuggester.suggestSwappers(player);
	}
}

