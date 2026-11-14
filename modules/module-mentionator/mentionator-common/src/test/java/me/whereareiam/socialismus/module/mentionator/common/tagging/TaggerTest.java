package me.whereareiam.socialismus.module.mentionator.common.tagging;

import me.whereareiam.conditia.model.Requirements;
import me.whereareiam.socialismus.model.chat.message.ChatMessage;
import me.whereareiam.socialismus.model.phrase.Phrase;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.module.mentionator.api.model.config.MentionatorMessages;
import me.whereareiam.socialismus.module.mentionator.api.model.config.MentionatorSettings;
import me.whereareiam.socialismus.module.mentionator.api.model.tag.Tag;
import me.whereareiam.socialismus.module.mentionator.api.model.tag.TagFormat;
import me.whereareiam.socialismus.module.mentionator.api.model.tag.TagParameters;
import me.whereareiam.socialismus.module.mentionator.api.model.tagging.TagUse;
import me.whereareiam.socialismus.module.mentionator.api.model.tagging.Tagging;
import me.whereareiam.socialismus.module.mentionator.common.condition.MentionatorConditions;
import me.whereareiam.socialismus.registry.WorkerProcessor;
import me.whereareiam.socialismus.service.condition.ConditionTexts;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static me.whereareiam.socialismus.module.mentionator.common.TestMentionator.conditions;
import static me.whereareiam.socialismus.module.mentionator.common.TestMentionator.message;
import static me.whereareiam.socialismus.module.mentionator.common.TestMentionator.permission;
import static me.whereareiam.socialismus.module.mentionator.common.TestMentionator.phrases;
import static me.whereareiam.socialismus.module.mentionator.common.TestMentionator.player;
import static me.whereareiam.socialismus.module.mentionator.common.TestMentionator.targeted;
import static me.whereareiam.socialismus.module.mentionator.common.TestMentionator.useMiniMessage;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TaggerTest {
	private final SocialismusPlayer alex = player("Alex", "staff");
	private final SocialismusPlayer steve = player("Steve");
	private final SocialismusPlayer bob = player("Bob");
	private final ConditionTexts texts = mock(ConditionTexts.class);
	private final Candidates candidates = mock(Candidates.class);
	private final MentionatorSettings settings = new MentionatorSettings();
	private final MentionatorMessages messages = new MentionatorMessages();
	private final List<Tag> tags = new ArrayList<>();
	private MentionatorConditions conditions;
	private Tagger tagger;

	@BeforeEach
	void setUp() {
		useMiniMessage();
		when(candidates.all()).thenReturn(List.of(
				new Candidate(alex, Map.of()),
				new Candidate(steve, Map.of()),
				new Candidate(bob, Map.of())
		));
		when(texts.denied(any(), anyString(), anyMap())).thenReturn(Optional.of(Phrase.of("refused")));
		messages.getLimits().setPerMessage("only {limit}");

		tags.add(Tag.builder()
				.id("player")
				.written(List.of("@{username}", "{username}"))
				.parameters(TagParameters.builder().minimumLength(3).build())
				.formats(List.of(
						new TagFormat("<aqua>@{match}</aqua>"),
						new TagFormat("<gold><bold>@{match}</bold></gold>", targeted())
				))
				.build());
		tags.add(Tag.builder()
				.id("everyone")
				.priority(10)
				.written(List.of("@everyone"))
				.parameters(TagParameters.builder().perMessage(1).build())
				.formats(List.of(new TagFormat("<red>@everyone</red>")))
				.requires(permission("staff"))
				.build());

		conditions = new MentionatorConditions(conditions(), phrases(), () -> tags, List::of, () -> settings);
		conditions.bind();
		tagger = new Tagger(conditions, candidates, () -> settings, () -> messages, texts, mock(WorkerProcessor.class));
	}

	@Test
	void aTaggedPlayerIsMarkedAndTheTagShownInItsPlainFormat() {
		ChatMessage message = tagger.tag(message(alex, "hi steve"));

		Tagging tagging = message.getAttribute(Tagging.ATTRIBUTE).orElseThrow();
		TagUse use = tagging.uses().getFirst();
		assertEquals("player", use.tag());
		assertEquals("steve", use.written());
		assertEquals("Steve", use.match());
		assertEquals(List.of(steve.getUniqueId()), use.targets());
		assertEquals("hi @Steve", plain(message.getContent()));
	}

	@Test
	void eachViewerSeesTheLastFormatTheyMeet() {
		ChatMessage message = tagger.tag(message(alex, "hi @Steve"));
		TagUse use = message.getAttribute(Tagging.ATTRIBUTE).orElseThrow().uses().getFirst();

		Component forSteve = TagRenderer.forViewer(conditions.catalog(), use, steve, message);
		Component forBob = TagRenderer.forViewer(conditions.catalog(), use, bob, message);

		assertEquals(NamedTextColor.GOLD, colorOf(forSteve));
		assertTrue(boldIn(forSteve));
		assertEquals(NamedTextColor.AQUA, colorOf(forBob));
	}

	@Test
	void aPlayerDoesNotTagThemselves() {
		ChatMessage message = tagger.tag(message(alex, "I am @Alex"));

		assertTrue(message.getAttribute(Tagging.ATTRIBUTE).isEmpty());
		assertEquals("I am @Alex", plain(message.getContent()));
	}

	@Test
	void aSenderWhoMayNotUseATagIsToldWhyAndItStaysPlain() {
		ChatMessage message = tagger.tag(message(steve, "@everyone hello"));

		assertTrue(message.getAttribute(Tagging.ATTRIBUTE).isEmpty());
		verify(steve).sendMessage(any());
	}

	@Test
	void aTagCountsOnlyAsOftenAsItsPerMessageAllows() {
		ChatMessage message = tagger.tag(message(alex, "@everyone @everyone"));

		assertEquals(1, message.getAttribute(Tagging.ATTRIBUTE).orElseThrow().uses().size());
		assertEquals("@everyone @everyone", plain(message.getContent()));
	}

	@Test
	void tagsPastTheLimitOfAMessageStayPlainAndTheSenderIsTold() {
		settings.getLimits().setPerMessage(List.of(
				new MentionatorSettings.PerMessage(1, Requirements.none()),
				new MentionatorSettings.PerMessage(-1, permission("unlimited"))
		));
		conditions.bind();

		ChatMessage message = tagger.tag(message(alex, "@Steve and @Bob"));

		List<TagUse> uses = message.getAttribute(Tagging.ATTRIBUTE).orElseThrow().uses();
		assertEquals(List.of("Steve"), uses.stream().map(TagUse::match).toList());
		assertEquals("@Steve and @Bob", plain(message.getContent()));
		verify(alex).sendMessage(any());
	}

	@Test
	void anEscapedTagLosesItsBackslashAndTagsNobody() {
		ChatMessage message = tagger.tag(message(alex, "type \\@everyone to tag all"));

		assertTrue(message.getAttribute(Tagging.ATTRIBUTE).isEmpty());
		assertEquals("type @everyone to tag all", plain(message.getContent()));
		verify(alex, never()).sendMessage(any());
	}

	@Test
	void aRelayedMessageIsNotTaggedAgain() {
		ChatMessage relayed = message(alex, "hi @Steve");
		relayed.setRelayed(true);

		assertFalse(tagger.tag(relayed).getAttribute(Tagging.ATTRIBUTE).isPresent());
	}

	private static String plain(Component component) {
		return PlainTextComponentSerializer.plainText().serialize(component);
	}

	private static NamedTextColor colorOf(Component component) {
		if (component.color() instanceof NamedTextColor named) return named;

		return component.children().stream().map(TaggerTest::colorOf).filter(java.util.Objects::nonNull).findFirst().orElse(null);
	}

	private static boolean boldIn(Component component) {
		return component.decoration(TextDecoration.BOLD) == TextDecoration.State.TRUE
				|| component.children().stream().anyMatch(TaggerTest::boldIn);
	}
}
