package me.whereareiam.socialismus.common.printer;

import com.google.inject.Inject;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.AnsiColor;
import me.whereareiam.socialismus.Constants;
import me.whereareiam.socialismus.common.container.ChatContainer;
import me.whereareiam.socialismus.common.provider.IntegrationProvider;
import me.whereareiam.socialismus.logging.LoggingHelper;
import me.whereareiam.socialismus.registry.ResourceRegistry;
import me.whereareiam.socialismus.service.CommandService;
import me.whereareiam.socialismus.type.PlatformType;
import me.whereareiam.socialismus.type.PluginType;
import me.whereareiam.socialismus.type.ResourceType;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class WelcomeBannerPrinter {
	private final LoggingHelper loggingHelper;
	private final CommandService commandService;
	private final ChatContainer chatContainer;
	private final IntegrationProvider integrationProvider;
	private final ResourceRegistry resourceRegistry;

	public void print() {
		List<String> lines = new ArrayList<>();
		lines.addAll(buildTitleLines());
		lines.addAll(buildStatsLines());
		lines.addAll(buildIntegrationLines());
		lines.addAll(buildConnectionLines());
		lines.forEach(loggingHelper::info);
	}

	private List<String> buildTitleLines() {
		List<String> l = new ArrayList<>();
		l.add("");
		l.add(AnsiColor.ORANGE +
				"  █▀ █▀▀   " + AnsiColor.RESET +
				"Socialismus v" + AnsiColor.GRAY +
				Constants.VERSION + AnsiColor.RESET);
		l.add(AnsiColor.ORANGE +
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
				AnsiColor.ORANGE + cmdCount + AnsiColor.RESET +
				" command" + (cmdCount == 1 ? "" : "s"));
		l.add("  Loaded " +
				AnsiColor.ORANGE + chatCount + AnsiColor.RESET +
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

	private List<String> buildConnectionLines() {
		List<String> l = new ArrayList<>();
		boolean db = resourceRegistry.has(ResourceType.DATABASE);
		boolean sync = resourceRegistry.has(ResourceType.SYNC);
		boolean cache = resourceRegistry.has(ResourceType.CACHE);

		if (db || sync || cache) {
			l.add("  Connections:");
			if (db) l.add(formatConn("Database"));
			if (sync) l.add(formatConn("Synchronization"));
			if (cache) l.add(formatConn("Cache"));
			l.add("");
		}

		return l;
	}

	private String formatConn(String label) {
		return String.format(
				"    - %s%s%s",
				AnsiColor.GREEN,
				label,
				AnsiColor.RESET
		);
	}
}
