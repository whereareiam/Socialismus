package me.whereareiam.socialismus.module.bubbler.common.requirement;

import me.whereareiam.conditia.ConditionScope;
import me.whereareiam.conditia.Conditions;
import me.whereareiam.conditia.common.ConditionsOptions;
import me.whereareiam.conditia.common.DefaultConditions;
import me.whereareiam.conditia.exception.ConditionException;
import me.whereareiam.conditia.testing.TestSettings;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.Bubble;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.BubbleMessage;
import me.whereareiam.socialismus.module.bubbler.api.type.ActivatorType;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static me.whereareiam.conditia.testing.TestRequirements.yaml;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class BubbleRequirementsTest {
	private final SocialismusPlayer alice = mock(SocialismusPlayer.class);
	private final List<Bubble> bubbles = new ArrayList<>();
	private final BubbleRequirements requirements = new BubbleRequirements(conditions(), () -> bubbles);

	@Test
	void selectsABubbleByWhatRaisedIt() {
		Bubble bubble = Bubble.builder().id("default").requires(yaml("""
				- any:
				    - bubbler:activator: COMMAND
				    - bubbler:chat: [fallback, local]
				""")).build();
		bubbles.add(bubble);
		requirements.bind();

		assertTrue(requirements.selects(bubble, message(ActivatorType.COMMAND, null)));
		assertTrue(requirements.selects(bubble, message(ActivatorType.CHAT, "local")));
		assertFalse(requirements.selects(bubble, message(ActivatorType.CHAT, "global")));
		assertTrue(requirements.reaches(bubble, alice, message(ActivatorType.CHAT, "global")));
	}

	@Test
	void aBubbleThatCannotBeBoundNamesItsList_AndTheBoundOnesStay() {
		Bubble bubble = Bubble.builder().id("default").build();
		bubbles.add(bubble);
		requirements.bind();
		bubbles.set(0, Bubble.builder().id("default").reaches(yaml("- bubbler:nearby: 5")).build());

		ConditionException exception = assertThrows(ConditionException.class, requirements::bind);

		assertEquals("bubbles.default.reaches[0].bubbler:nearby", exception.getPath());
		assertTrue(requirements.selects(bubble, message(ActivatorType.CHAT, "local")));
	}

	private BubbleMessage message(ActivatorType activator, String chat) {
		return BubbleMessage.builder().sender(alice).activatorType(activator).chat(chat).build();
	}

	private static Conditions conditions() {
		Conditions conditions = new DefaultConditions(ConditionsOptions.builder()
				.reader(new TestSettings()
						.read(ActivatorCondition.Settings.class, node -> new ActivatorCondition.Settings(
								TestSettings.texts(node, "activators").stream().map(ActivatorType::valueOf).toList()))
						.read(ChatActivationCondition.Settings.class, node -> new ChatActivationCondition.Settings(TestSettings.texts(node, "chats"))))
				.build());
		// As Bubbler registers them in its module's scope.
		ConditionScope scope = conditions.scope("bubbler");
		scope.register(new ActivatorCondition());
		scope.register(new ChatActivationCondition());

		return conditions;
	}
}
