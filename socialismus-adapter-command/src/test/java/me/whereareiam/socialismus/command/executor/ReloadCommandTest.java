package me.whereareiam.socialismus.command.executor;

import me.whereareiam.keystone.Actor;
import me.whereareiam.keystone.model.SerializerContent;
import me.whereareiam.keystone.serializer.SerializerEngine;
import me.whereareiam.socialismus.Reloadable;
import me.whereareiam.socialismus.Serializer;
import me.whereareiam.socialismus.event.EventManager;
import me.whereareiam.socialismus.event.plugin.PluginReloadedEvent;
import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.logging.LoggingHelper;
import me.whereareiam.socialismus.model.config.message.CommandMessages;
import me.whereareiam.socialismus.model.config.message.Messages;
import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("Reload command")
class ReloadCommandTest {
	private final List<String> answers = new ArrayList<>();
	private final List<String> reloaded = new ArrayList<>();
	private final EventManager eventManager = mock(EventManager.class);

	@BeforeEach
	void recordAnswers() {
		Logger.init(mock(LoggingHelper.class));

		SerializerEngine engine = mock(SerializerEngine.class);
		when(engine.serialize(any(Actor.class), anyString())).thenAnswer(call -> {
			answers.add(call.getArgument(1, String.class));
			return Component.empty();
		});
		when(engine.serialize(any(SerializerContent.class))).thenAnswer(call -> {
			SerializerContent content = call.getArgument(0, SerializerContent.class);
			answers.add(content.getMessage() + ": " + content.getPlaceholder("exception"));
			return Component.empty();
		});
		Serializer.initialize(() -> engine);
	}

	@DisplayName("Reloads every document in the order they registered and answers with success")
	@Test
	void reloadsEveryDocumentInOrder() {
		command(reloadable("settings"), reloadable("messages"), reloadable("chats")).command(mock(Actor.class));

		assertEquals(List.of("settings", "messages", "chats"), reloaded);
		assertEquals(List.of("reloaded"), answers);
	}

	@DisplayName("A document that fails keeps the others reloading and the answer names the failure")
	@Test
	void failureIsReportedAfterTheOthersReloaded() {
		Reloadable broken = () -> {
			throw new IllegalStateException("Failed to read chats.yml");
		};

		command(reloadable("settings"), broken, reloadable("messages")).command(mock(Actor.class));

		assertEquals(List.of("settings", "messages"), reloaded);
		assertEquals(List.of("exception: Failed to read chats.yml"), answers);
	}

	@DisplayName("Calls the reloaded event once, after every document, also when one failed")
	@Test
	void reloadedEventFollowsTheDocuments() {
		doAnswer(call -> {
			assertInstanceOf(PluginReloadedEvent.class, call.getArgument(0));
			reloaded.add("event");
			return null;
		}).when(eventManager).call(any());
		Reloadable broken = () -> {
			throw new IllegalStateException("Failed to read chats.yml");
		};

		command(reloadable("settings"), broken, reloadable("messages")).command(mock(Actor.class));

		assertEquals(List.of("settings", "messages", "event"), reloaded);
	}

	private Reloadable reloadable(String name) {
		return () -> reloaded.add(name);
	}

	private ReloadCommand command(Reloadable... reloadables) {
		Set<Reloadable> ordered = new LinkedHashSet<>(List.of(reloadables));
		return new ReloadCommand(ReloadCommandTest::messages, () -> ordered, eventManager);
	}

	private static Messages messages() {
		CommandMessages.ReloadCommand reload = new CommandMessages.ReloadCommand();
		reload.setReloaded("reloaded");
		reload.setException("exception");
		CommandMessages commands = new CommandMessages();
		commands.setReloadCommand(reload);
		Messages messages = new Messages();
		messages.setCommands(commands);
		return messages;
	}
}
