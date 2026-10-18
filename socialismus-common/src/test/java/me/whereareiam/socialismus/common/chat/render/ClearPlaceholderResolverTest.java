package me.whereareiam.socialismus.common.chat.render;

import com.google.inject.Provider;
import me.whereareiam.socialismus.model.chat.ChatMessages;
import me.whereareiam.socialismus.model.chat.ChatSettings;
import me.whereareiam.socialismus.model.chat.message.FormattedChatMessage;
import me.whereareiam.socialismus.model.chat.render.ChatRenderContext;
import me.whereareiam.socialismus.model.config.Commands;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.model.requirement.Requirement;
import me.whereareiam.socialismus.model.requirement.RequirementGroup;
import me.whereareiam.socialismus.registry.base.Registry;
import me.whereareiam.socialismus.service.chat.render.ChatPlaceholderResolver;
import me.whereareiam.socialismus.service.requirement.RequirementEvaluatorService;
import me.whereareiam.socialismus.type.requirement.RequirementOperatorType;
import me.whereareiam.socialismus.util.ComponentUtil;
import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.withSettings;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClearPlaceholderResolverTest {
	private static final RequirementGroup CLEARERS = RequirementGroup.builder()
			.operator(RequirementOperatorType.AND)
			.groups(Map.of("test:clearers", new Requirement()))
			.build();
	private static final RequirementGroup PROTECTED = RequirementGroup.builder()
			.operator(RequirementOperatorType.AND)
			.groups(Map.of("test:protected", new Requirement()))
			.build();

	@Mock
	private Provider<ChatSettings> chatSettings;
	@Mock
	private Provider<ChatMessages> chatMessages;
	@Mock
	private Provider<Commands> commands;
	@Mock
	private RequirementEvaluatorService requirementEvaluator;
	@Mock
	private Registry<ChatPlaceholderResolver> registry;

	@Test
	void resolveReturnsEmptyWhenRecipientMissing() {
		ClearPlaceholderResolver resolver = new ClearPlaceholderResolver(
				chatSettings,
				chatMessages,
				commands,
				requirementEvaluator,
				registry
		);

		FormattedChatMessage message = FormattedChatMessage.builder().id(1).build();
		Component resolved = resolver.resolve(new ChatRenderContext(message, null));

		assertEquals("", ComponentUtil.toPlain(resolved));
	}

	@Test
	void resolveReturnsEmptyWhenRecipientDoesNotMeetTheRequirements() {
		ChatSettings settings = new ChatSettings();
		ChatSettings.ChatHistorySettings history = new ChatSettings.ChatHistorySettings();
		history.setRequirements(CLEARERS);
		settings.setHistory(history);

		ChatMessages messages = new ChatMessages();
		ChatMessages.ClearFormat clearFormat = new ChatMessages.ClearFormat();
		clearFormat.setFormat("<gray>[clear]</gray>");
		messages.setClearFormat(clearFormat);

		when(chatSettings.get()).thenReturn(settings);
		when(chatMessages.get()).thenReturn(messages);

		SocialismusPlayer recipient = mock(SocialismusPlayer.class,
				withSettings().useConstructor(UUID.randomUUID(), "recipient").defaultAnswer(CALLS_REAL_METHODS));
		when(requirementEvaluator.check(CLEARERS, recipient)).thenReturn(false);

		ClearPlaceholderResolver resolver = new ClearPlaceholderResolver(
				chatSettings,
				chatMessages,
				commands,
				requirementEvaluator,
				registry
		);

		FormattedChatMessage message = FormattedChatMessage.builder().id(2).build();
		Component resolved = resolver.resolve(new ChatRenderContext(message, recipient));

		assertEquals("", ComponentUtil.toPlain(resolved));
	}

	@Test
	void resolveReturnsEmptyForTheMessageOfASenderWhoMeetsTheBypassRequirements() {
		ChatSettings settings = new ChatSettings();
		ChatSettings.ChatHistorySettings history = new ChatSettings.ChatHistorySettings();
		history.setRequirements(CLEARERS);
		history.setBypassRequirements(PROTECTED);
		settings.setHistory(history);

		ChatMessages messages = new ChatMessages();
		ChatMessages.ClearFormat clearFormat = new ChatMessages.ClearFormat();
		clearFormat.setFormat("<gray>[clear]</gray>");
		messages.setClearFormat(clearFormat);

		when(chatSettings.get()).thenReturn(settings);
		when(chatMessages.get()).thenReturn(messages);

		SocialismusPlayer recipient = mock(SocialismusPlayer.class,
				withSettings().useConstructor(UUID.randomUUID(), "recipient").defaultAnswer(CALLS_REAL_METHODS));
		SocialismusPlayer sender = mock(SocialismusPlayer.class,
				withSettings().useConstructor(UUID.randomUUID(), "sender").defaultAnswer(CALLS_REAL_METHODS));
		when(requirementEvaluator.check(CLEARERS, recipient)).thenReturn(true);
		when(requirementEvaluator.check(PROTECTED, sender)).thenReturn(true);

		ClearPlaceholderResolver resolver = new ClearPlaceholderResolver(
				chatSettings,
				chatMessages,
				commands,
				requirementEvaluator,
				registry
		);

		FormattedChatMessage message = FormattedChatMessage.builder().id(3).sender(sender).build();
		Component resolved = resolver.resolve(new ChatRenderContext(message, recipient));

		assertEquals("", ComponentUtil.toPlain(resolved));
		verify(requirementEvaluator).check(PROTECTED, sender);
	}
}
