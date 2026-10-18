package me.whereareiam.socialismus.common.requirement;

import me.whereareiam.socialismus.Constants;
import me.whereareiam.socialismus.model.config.message.Messages;
import me.whereareiam.socialismus.model.config.message.RequirementReasons;
import me.whereareiam.socialismus.model.requirement.Requirement;
import me.whereareiam.socialismus.model.requirement.RequirementDenial;
import me.whereareiam.socialismus.model.requirement.RequirementKey;
import me.whereareiam.socialismus.model.requirement.RequirementReason;
import me.whereareiam.socialismus.model.requirement.RequirementResult;
import me.whereareiam.socialismus.type.requirement.RequirementFailure;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class RequirementReasonResolverTest {
	private static final String WORLD = Constants.Requirements.WORLD.getFullKey();
	private static final RequirementKey<Requirement> MUTED = RequirementKey.create("litebans", "muted", Requirement.class);
	private static final Map<String, String> CHAT = Map.of("chat", "staff");

	private final Messages messages = new Messages();
	private final RequirementReasonResolver resolver = new RequirementReasonResolver(() -> messages);

	@BeforeEach
	void setUp() {
		messages.setRequirements(Map.of(WORLD, reasons("Core: only in {worlds}", "Core: not in {world}")));
	}

	@Test
	void tellsTheGroupsOwnReasonFirstWithEveryPlaceholder() {
		RequirementResult result = RequirementResult.denied(worldDenial(RequirementFailure.REQUIRED), "{chat} lives in {worlds}");

		RequirementReason reason = resolver.resolve(result, Map.of(WORLD, reasons("Chat: only in {worlds}", null)), CHAT);

		assertEquals(RequirementReason.of(
				"{chat} lives in {worlds}",
				Map.of("chat", "staff", "worlds", "world", "world", "world_nether")
		), reason);
	}

	@Test
	void tellsTheFeaturesWordingBeforeTheCoreOne() {
		RequirementResult result = RequirementResult.denied(worldDenial(RequirementFailure.FORBIDDEN));

		RequirementReason reason = resolver.resolve(result, Map.of(WORLD, reasons("Chat: only in {worlds}", "Chat: not in {world}")), CHAT);

		assertEquals("Chat: not in {world}", reason.getMessage());
	}

	@Test
	void tellsTheCoreWordingWhereTheFeatureHasNoneOrABlankOne() {
		RequirementResult result = RequirementResult.denied(worldDenial(RequirementFailure.REQUIRED));

		RequirementReason unworded = resolver.resolve(result, Map.of(), CHAT);
		RequirementReason blank = resolver.resolve(result, Map.of(WORLD, reasons(" ", "Chat: not in {world}")), CHAT);
		RequirementReason noFeature = resolver.resolve(result, null, CHAT);

		assertEquals("Core: only in {worlds}", unworded.getMessage());
		assertEquals("Core: only in {worlds}", blank.getMessage());
		assertEquals("Core: only in {worlds}", noFeature.getMessage());
	}

	@Test
	void tellsTheRequirementsOwnMessageWhereNobodyWordsItsKey() {
		RequirementDenial denial = RequirementDenial.of(MUTED, RequirementFailure.FORBIDDEN, Map.of("until", "12:30"))
				.withMessage("Muted until {until}");

		RequirementReason reason = resolver.resolve(RequirementResult.denied(denial), Map.of(WORLD, reasons("Chat", "Chat")), CHAT);

		assertEquals(RequirementReason.of("Muted until {until}", Map.of("until", "12:30", "chat", "staff")), reason);
	}

	@Test
	void tellsNothingWhenNothingWordsTheDenialOrTheResultPassed() {
		RequirementResult muted = RequirementResult.denied(RequirementDenial.of(MUTED, RequirementFailure.FORBIDDEN, Map.of()));

		assertNull(resolver.resolve(muted, null, CHAT));
		assertNull(resolver.resolve(RequirementResult.denied(), Map.of(WORLD, reasons("Chat", "Chat")), CHAT));
		assertNull(resolver.resolve(RequirementResult.passed(), Map.of(WORLD, reasons("Chat", "Chat")), CHAT));
	}

	@Test
	void keepsTheMeaningOfTheRequirementsPlaceholders() {
		RequirementResult result = RequirementResult.denied(worldDenial(RequirementFailure.REQUIRED));

		RequirementReason reason = resolver.resolve(result, null, Map.of("worlds", "a feature's worlds"));

		assertEquals("world", reason.getPlaceholders().get("worlds"));
	}

	private static RequirementDenial worldDenial(RequirementFailure failure) {
		return RequirementDenial.of(
				Constants.Requirements.WORLD,
				failure,
				Map.of("worlds", "world", "world", "world_nether")
		);
	}

	private static RequirementReasons reasons(String required, String forbidden) {
		RequirementReasons reasons = new RequirementReasons();
		reasons.setRequired(required);
		reasons.setForbidden(forbidden);

		return reasons;
	}
}
