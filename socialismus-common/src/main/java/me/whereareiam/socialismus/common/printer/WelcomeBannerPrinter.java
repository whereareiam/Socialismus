package me.whereareiam.socialismus.common.printer;

import com.google.inject.Inject;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.api.AnsiColor;
import me.whereareiam.socialismus.api.output.LoggingHelper;
import me.whereareiam.socialismus.api.output.command.CommandService;
import me.whereareiam.socialismus.api.type.PlatformType;
import me.whereareiam.socialismus.api.type.PluginType;
import me.whereareiam.socialismus.common.container.ChatContainer;
import me.whereareiam.socialismus.common.provider.IntegrationProvider;
import me.whereareiam.socialismus.shared.Constants;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class WelcomeBannerPrinter {
	private final LoggingHelper loggingHelper;
	private final CommandService commandService;
	private final ChatContainer chatContainer;
	private final IntegrationProvider integrationProvider;

	public void print() {
		List<String> lines = new ArrayList<>();
		lines.addAll(buildTitleLines());
		lines.addAll(buildStatsLines());
		lines.addAll(buildIntegrationLines());
		lines.forEach(loggingHelper::info);
	}

	private List<String> buildTitleLines() {
		List<String> l = new ArrayList<>();
		l.add("");
		l.add(AnsiColor.CYAN +
				"  █▀ █▀▀   " + AnsiColor.RESET +
				"Socialismus v" + AnsiColor.GRAY +
				Constants.VERSION + AnsiColor.RESET);
		l.add(AnsiColor.CYAN +
				"  ▄█ █▄▄   " + AnsiColor.RESET +
				"Platform: " + AnsiColor.GRAY +
				PlatformType.getType() + " [" +
				PluginType.getType() + "]" +
				AnsiColor.RESET);
		l.add("");
		return l;
	}

	private List<String> buildStatsLines() {
		List<String> l = new ArrayList<>();
		int cmdCount = commandService.getCommandCount();
		int chatCount = chatContainer.getChats().size();

		l.add("  Loaded " +
				AnsiColor.CYAN + cmdCount + AnsiColor.RESET +
				" command" + (cmdCount == 1 ? "" : "s"));
		l.add("  Loaded " +
				AnsiColor.CYAN + chatCount + AnsiColor.RESET +
				" chat" + (chatCount == 1 ? "" : "s"));
		l.add("");
		return l;
	}

	private List<String> buildIntegrationLines() {
		List<String> l = new ArrayList<>();
		l.add("  Integrations:");
		integrationProvider.get().forEach(i ->
				l.add("    - " +
						AnsiColor.GREEN + i.getName() +
						AnsiColor.RESET));
		l.add("");
		return l;
	}
}
