package me.whereareiam.socialismus.common.chat.worker.base;

import me.whereareiam.conditia.model.Part;
import me.whereareiam.conditia.result.Verdict;
import me.whereareiam.socialismus.model.chat.Chat;
import me.whereareiam.socialismus.model.phrase.Phrase;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.service.chat.ChatConditionService;
import me.whereareiam.socialismus.service.condition.ConditionTexts;
import me.whereareiam.socialismus.service.phrase.PhraseScope;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class NobodyNoticeTest {
	private final ConditionTexts texts = mock(ConditionTexts.class);
	private final PhraseScope phrases = mock(PhraseScope.class);
	private final ChatConditionService conditions = mock(ChatConditionService.class);
	private final NobodyNotice notice = new NobodyNotice(texts, conditions);

	NobodyNoticeTest() {
		when(conditions.phrases()).thenReturn(phrases);
	}

	@Test
	void mostCommon_TakesTheLineThatRefusedMost_AndTheEarlierOfTwoThatRefusedAsMany() {
		Verdict.Failed permission = refused("chats.local.reaches[0].permission");
		Verdict.Failed nearby = refused("chats.local.reaches[1].nearby");

		assertEquals(Optional.of(nearby), NobodyNotice.mostCommon(List.of(permission, nearby, nearby)));
		assertEquals(Optional.of(permission), NobodyNotice.mostCommon(List.of(nearby, permission)));
		assertEquals(Optional.empty(), NobodyNotice.mostCommon(List.of()));
	}

	@Test
	void tell_FallsBackToChatNobody_WhenNothingWordsTheReason() {
		SocialismusPlayer sender = mock(SocialismusPlayer.class);
		Chat chat = Chat.builder().id("local").build();
		when(texts.denied(any(), eq(phrases), anyMap())).thenReturn(Optional.empty());
		when(phrases.phrase("nobody", Map.of("chat", "local"))).thenReturn(Optional.empty());

		notice.tell(sender, chat, List.of(refused("chats.local.reaches[0].nearby")));

		verify(phrases).phrase("nobody", Map.of("chat", "local"));
		verify(sender, never()).sendMessage(any());
	}

	@Test
	void tell_WordsTheReasonOfTheLine() {
		Verdict.Failed nearby = refused("chats.local.reaches[0].nearby");
		when(texts.denied(eq(nearby), eq(phrases), anyMap())).thenReturn(Optional.of(Phrase.of("No one heard you.")));

		notice.tell(mock(SocialismusPlayer.class), Chat.builder().id("local").build(), List.of(nearby));

		verify(phrases, never()).phrase(any(), anyMap());
	}

	private static Verdict.Failed refused(String path) {
		return new Verdict.Failed(path, "nearby", List.<Part<?>>of(), Optional.empty());
	}
}
