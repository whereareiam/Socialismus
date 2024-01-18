package me.whereareiam.socialismus.core.listener.listeners;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.event.bubblechat.BeforeBubbleSendMessageEvent;
import me.whereareiam.socialismus.api.model.BubbleMessage;
import me.whereareiam.socialismus.core.module.swapper.SwapperService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

@Singleton
public class BeforeBubbleSendMessageListener implements Listener {
	private final SwapperService swapperService;

	@Inject
	public BeforeBubbleSendMessageListener(SwapperService swapperService) {this.swapperService = swapperService;}

	@EventHandler
	public void onBubbleSendMessage(BeforeBubbleSendMessageEvent event) {
		BubbleMessage bubbleMessage = event.getBubbleMessage();

		event.setBubbleMessage(swapperService.swapPlaceholders(bubbleMessage));
	}
}
