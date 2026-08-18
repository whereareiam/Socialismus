package me.whereareiam.socialismus.module.bubbler.common.config.defaults;

import com.google.inject.Singleton;
import me.whereareiam.configura.merge.defaults.DefaultsProvider;
import me.whereareiam.socialismus.Constants;
import me.whereareiam.socialismus.model.requirement.RequirementGroup;
import me.whereareiam.socialismus.model.requirement.type.ChatRequirement;
import me.whereareiam.socialismus.module.bubbler.api.model.Vector;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.Bubble;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.BubbleTransition;
import me.whereareiam.socialismus.module.bubbler.api.type.AlignmentType;
import me.whereareiam.socialismus.module.bubbler.api.type.AnimationType;
import me.whereareiam.socialismus.module.bubbler.api.type.DisplayType;
import me.whereareiam.socialismus.module.bubbler.api.type.TransitionType;
import me.whereareiam.socialismus.module.bubbler.common.config.dynamic.BubblesConfig;
import me.whereareiam.socialismus.type.chat.Participants;
import me.whereareiam.socialismus.type.requirement.RequirementConditionType;
import me.whereareiam.socialismus.type.requirement.RequirementOperatorType;

import java.util.List;
import java.util.Map;

@Singleton
public class BubblesDefaults implements DefaultsProvider<BubblesConfig> {
	@Override
	public BubblesConfig supply(BubblesConfig config) {
		// Default values
		Bubble bubble = Bubble.builder()
				.id("default")
				.enabled(true)
				.display(Bubble.Display.builder()
						.radius(50)
						.headLineGap(0.3F)
						.lineSpacing(0.2F)
						.maxLinesCount(5)
						.maxLineWidth(30)
						.timePerSymbol(0.12)
						.minimumTime(1.5)
						.build()
				).format(Bubble.Format.builder()
						.format("<white>{message}")
						.initialFormat("<gold>{playerName} <white>says: \n")
						.finalFormat("<white>.")
						.queuedFormat("<white>...")
						.separatorFormat("-")
						.build()
				).style(Bubble.Style.builder()
						.animation(AnimationType.POPOUT)
						.display(DisplayType.CENTER)
						.seeThrough(true)
						.scale(new Vector(1.0f, 1.0f, 1.0f))
						.background(Bubble.Style.BackgroundStyle.builder()
								.color("#000000")
								.transparency((short) 0)
								.build()
						).text(Bubble.Style.TextStyle.builder()
								.alignment(AlignmentType.CENTER)
								.shadow(false)
								.build()
						).build()
				).transitions(Map.of(
						TransitionType.BEFORE, BubbleTransition.builder()
								.sound(BubbleTransition.Sound.builder().type("BLOCK_BELL_USE").pitch(1.0f).volume(1.0f).build())
								.build(),
						TransitionType.INTER, BubbleTransition.builder()
								.sound(BubbleTransition.Sound.builder().type("ENTITY_CHICKEN_EGG").pitch(1.0f).volume(1.0f).build())
								.build()
				)).requirements(Map.of(
						Participants.SENDER, RequirementGroup.builder()
								.operator(RequirementOperatorType.OR)
								.groups(RequirementGroup.of(
										Constants.Requirements.CHAT,
										ChatRequirement.builder()
												.condition(RequirementConditionType.CONTAINS)
												.expected("true")
												.chatIdentifiers(List.of("null", "fallback", "local"))
												.build()
								)).build()
				)).build();

		config.getBubbles().add(bubble);

		return config;
	}
}
