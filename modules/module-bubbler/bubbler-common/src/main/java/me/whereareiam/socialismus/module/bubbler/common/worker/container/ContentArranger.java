package me.whereareiam.socialismus.module.bubbler.common.worker.container;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.Serializer;
import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.model.Worker;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.Bubble;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.BubbleGroup;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.BubbleLine;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.BubbleMessage;
import me.whereareiam.socialismus.module.bubbler.api.type.AnimationType;
import me.whereareiam.socialismus.registry.WorkerProcessor;
import me.whereareiam.socialismus.util.ComponentUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;

import java.util.*;

@Singleton
public class ContentArranger {
	private static final Set<AnimationType> SINGLE_GROUP_ANIMATIONS = EnumSet.of(
			AnimationType.POPOUT, AnimationType.EXPANSION, AnimationType.CONTRACTION
	);

	@Inject
	public ContentArranger(WorkerProcessor<BubbleMessage> workerProcessor) {
		workerProcessor.addWorker(new Worker<>(this::arrangeContent, 150, false, false));
	}

	public BubbleMessage arrangeContent(BubbleMessage bubbleMessage) {
		Logger.debug("Arranging content for " + bubbleMessage.getSender().getUsername());

		String content = ComponentUtil.toLegacy(bubbleMessage.getContent(), true);
		List<String> lines = formatContent(bubbleMessage, content);

		Bubble bubble = bubbleMessage.getBubble();
		List<BubbleGroup> groups;

		if (SINGLE_GROUP_ANIMATIONS.contains(bubble.getStyle().getAnimation())) {
			groups = createSingleGroup(bubbleMessage, lines);
		} else {
			groups = createMultiLineGroups(bubbleMessage, lines);
		}

		Logger.debug("Putting " + lines.size() + " lines into " + groups.size() + " groups");
		bubbleMessage.setGroups(new LinkedList<>(groups));

		return bubbleMessage;
	}

	private List<BubbleGroup> createSingleGroup(BubbleMessage bubbleMessage, List<String> lines) {
		List<BubbleLine> bubbleLines = new ArrayList<>(lines.size());
		for (String text : lines) {
			bubbleLines.add(BubbleLine.builder()
					.content(line(bubbleMessage, text))
					.build());
		}

		applyFormats(bubbleMessage, bubbleLines);
		Collections.reverse(bubbleLines);
		return List.of(BubbleGroup.builder().lines(bubbleLines).build());
	}

	private List<BubbleGroup> createMultiLineGroups(BubbleMessage bubbleMessage, List<String> lines) {
		int max = bubbleMessage.getBubble().getDisplay().getMaxLinesCount();
		List<BubbleGroup> groups = new ArrayList<>();
		BubbleGroup current = BubbleGroup.builder().lines(new ArrayList<>()).build();

		for (int i = 0; i < lines.size(); i++) {
			String txt = lines.get(i);
			BubbleLine bl = BubbleLine.builder()
					.content(line(bubbleMessage, txt))
					.build();
			current.getLines().add(bl);

			if (current.getLines().size() >= max && i < lines.size() - 1) {
				applyQueuedFormatToLastLine(bubbleMessage, current.getLines());
				applyFormats(bubbleMessage, current.getLines());
				Collections.reverse(current.getLines());
				groups.add(current);
				current = BubbleGroup.builder().lines(new ArrayList<>()).build();
			}
		}

		if (!current.getLines().isEmpty()) {
			applyFormats(bubbleMessage, current.getLines());
			Collections.reverse(current.getLines());
			groups.add(current);
		}
		return groups;
	}

	private void applyQueuedFormatToLastLine(BubbleMessage bubbleMessage, List<BubbleLine> bubbleLines) {
		if (bubbleLines.isEmpty()) return;

		String queuedFormat = bubbleMessage.getBubble().getFormat().getQueuedFormat();
		if (queuedFormat == null || queuedFormat.isEmpty()) return;

		int lastIndex = bubbleLines.size() - 1;
		BubbleLine lastLine = bubbleLines.get(lastIndex);

		BubbleLine updatedLine = BubbleLine.builder()
				.content(
						lastLine.getContent().append(
								Serializer.serialize(bubbleMessage.getSender(), queuedFormat)
						)
				)
				.build();

		bubbleLines.set(lastIndex, updatedLine);
	}

	private void applyFormats(BubbleMessage bubbleMessage, List<BubbleLine> bubbleLines) {
		if (bubbleLines.isEmpty()) return;

		Bubble bubble = bubbleMessage.getBubble();
		String initialFormat = bubble.getFormat().getInitialFormat();
		String finalFormat = bubble.getFormat().getFinalFormat();

		applyFormatToLine(initialFormat, bubbleLines, bubbleMessage.getSender(), true);
		applyFormatToLine(finalFormat, bubbleLines, bubbleMessage.getSender(), false);
	}

	private void applyFormatToLine(
			String format,
			List<BubbleLine> lines,
			SocialismusPlayer sender,
			boolean isInitial
	) {
		if (format.contains("\n")) {
			String[] parts = format.split("\n");
			List<BubbleLine> newLines = new ArrayList<>();
			for (String part : parts) {
				newLines.add(BubbleLine.builder()
						.content(Serializer.serialize(sender, part))
						.build());
			}
			if (isInitial) newLines.addAll(lines);
			else newLines.addAll(0, lines);

			lines.clear();
			lines.addAll(newLines);
		} else {
			int idx = isInitial ? 0 : lines.size() - 1;
			BubbleLine old = lines.get(idx);
			lines.set(idx,
					BubbleLine.builder()
							.content(isInitial
									? Serializer.serialize(sender, format).append(old.getContent())
									: old.getContent().append(Serializer.serialize(sender, format)))
							.build()
			);
		}
	}

	/**
	 * Puts a line of what the sender wrote into the bubble's format. The line goes in after the format is parsed, so
	 * it shows as its chat or style read it and is never read as formatting or placeholders again.
	 */
	private static Component line(BubbleMessage bubbleMessage, String text) {
		Component content = ComponentUtil.getLEGACY_SECTION_SERIALIZER().deserialize(text);

		return Serializer.serialize(bubbleMessage.getSender(), bubbleMessage.getBubble().getFormat().getFormat())
				.replaceText(TextReplacementConfig.builder()
						.matchLiteral(Serializer.placeholder("message"))
						.replacement(content)
						.build());
	}

	private List<String> formatContent(BubbleMessage bubbleMessage, String content) {
		List<String> lines = new ArrayList<>();
		Bubble bubble = bubbleMessage.getBubble();
		int maxWidth = bubble.getDisplay().getMaxLineWidth();
		String[] words = content.split(" ");
		StringBuilder curr = new StringBuilder();

		for (String w : words) {
			if (curr.length() + w.length() > maxWidth) {
				if (w.length() > maxWidth) {
					while (w.length() > maxWidth) {
						int partLen = maxWidth - curr.length();
						if (partLen <= 0) {
							lines.add(curr.toString().trim());
							curr = new StringBuilder();
							partLen = maxWidth;
						}
						String part = w.substring(0, partLen);
						w = w.substring(partLen);
						curr.append(part).append(bubble.getFormat().getSeparatorFormat());
						lines.add(curr.toString().trim());
						curr = new StringBuilder();
					}
				} else {
					lines.add(curr.toString().trim());
					curr = new StringBuilder();
				}
			}
			curr.append(w).append(" ");
		}
		if (!curr.isEmpty()) {
			lines.add(curr.toString().trim());
		}
		return lines;
	}
}
