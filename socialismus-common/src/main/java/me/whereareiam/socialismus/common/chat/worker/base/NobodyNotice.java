package me.whereareiam.socialismus.common.chat.worker.base;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.conditia.result.Verdict;
import me.whereareiam.socialismus.Serializer;
import me.whereareiam.socialismus.model.chat.Chat;
import me.whereareiam.socialismus.model.phrase.Phrase;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.service.Phrasebook;
import me.whereareiam.socialismus.service.chat.ChatConditionService;
import me.whereareiam.socialismus.service.condition.ConditionTexts;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Tells a sender why their message reached nobody: the line of the chat's {@code reaches} that refused the most
 * players, the earlier line when two refused as many, worded as a chat words a recipient's refusal; or else
 * {@code chat.nobody}.
 */
@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class NobodyNotice {
	private static final String NOBODY = "nobody";

	private final ConditionTexts texts;
	private final Phrasebook phrasebook;

	/**
	 * Tells the sender why the message reached nobody.
	 *
	 * @param sender   the sender
	 * @param chat     the chat of the message
	 * @param refusals the refusals of the players the chat's {@code reaches} did not let through
	 */
	public void tell(SocialismusPlayer sender, Chat chat, List<Verdict.Failed> refusals) {
		Map<String, String> placeholders = Map.of("chat", chat.getId());
		Optional<Phrase> phrase = mostCommon(refusals)
				.flatMap(refusal -> texts.denied(refusal, ChatConditionService.PHRASE_SCOPE, placeholders))
				.or(() -> phrasebook.phrase(ChatConditionService.PHRASE_SCOPE, NOBODY, placeholders));

		phrase.ifPresent(found -> sender.sendMessage(Serializer.serialize(found.toContent(sender))));
	}

	static Optional<Verdict.Failed> mostCommon(List<Verdict.Failed> refusals) {
		Map<String, List<Verdict.Failed>> byLine = refusals.stream()
				.collect(Collectors.groupingBy(Verdict.Failed::path, TreeMap::new, Collectors.toList()));

		// maxBy keeps the first of equals, and the lines come in the order of their paths.
		return byLine.values().stream()
				.max(Comparator.comparingInt(List::size))
				.map(List::getFirst);
	}
}
