package me.whereareiam.socialismus.common.requirement;

import me.whereareiam.socialismus.Constants;
import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.logging.LoggingHelper;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.model.requirement.Requirement;
import me.whereareiam.socialismus.model.requirement.RequirementDenial;
import me.whereareiam.socialismus.model.requirement.RequirementGroup;
import me.whereareiam.socialismus.model.requirement.RequirementKey;
import me.whereareiam.socialismus.model.requirement.RequirementResult;
import me.whereareiam.socialismus.model.requirement.type.PermissionRequirement;
import me.whereareiam.socialismus.registry.base.ExtendedRegistry;
import me.whereareiam.socialismus.service.requirement.RequirementValidation;
import me.whereareiam.socialismus.type.requirement.RequirementConditionType;
import me.whereareiam.socialismus.type.requirement.RequirementFailure;
import me.whereareiam.socialismus.type.requirement.RequirementOperatorType;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("RequirementEvaluator Tests")
class RequirementEvaluatorTest {

    private RequirementEvaluator evaluator;
    private RequirementRegistry registry;
    
    @Mock
    private SocialismusPlayer mockPlayer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Initialize Logger with a mock for testing
        Logger.init(mock(LoggingHelper.class));
        
        registry = new RequirementRegistry();
        evaluator = new RequirementEvaluator(registry);
        
        when(mockPlayer.getUsername()).thenReturn("TestPlayer");
    }

    @Test
    @DisplayName("Should return true for null group")
    void testNullGroup() {
        // When
        boolean result = evaluator.check(null, mockPlayer);

        // Then
        assertTrue(result);
    }

    @Test
    @DisplayName("Should return true for empty group")
    void testEmptyGroup() {
        // Given
        RequirementGroup group = RequirementGroup.builder()
                .operator(RequirementOperatorType.AND)
                .groups(new HashMap<>())
                .build();

        // When
        boolean result = evaluator.check(group, mockPlayer);

        // Then
        assertTrue(result);
    }

    @Test
    @DisplayName("Should return false when no validator registered")
    void testNoValidatorRegistered() {
        // Given
        Map<String, Requirement> requirements = new HashMap<>();
        requirements.put(Constants.Requirements.PERMISSION.getFullKey(), PermissionRequirement.builder()
                .condition(RequirementConditionType.HAS)
                .expected("true")
                .permissions(List.of("test.permission"))
                .build());

        RequirementGroup group = RequirementGroup.builder()
                .operator(RequirementOperatorType.AND)
                .groups(requirements)
                .build();

        // When
        boolean result = evaluator.check(group, mockPlayer);

        // Then
        assertFalse(result);
    }

    @Test
    @DisplayName("AND operator - should return true when all requirements met")
    void testAndOperatorAllMet() {
        // Given
        RequirementValidation alwaysTrueValidator = (req, player) -> RequirementResult.passed();
        registry.register(Constants.Requirements.PERMISSION, alwaysTrueValidator);
        registry.register(Constants.Requirements.SERVER, alwaysTrueValidator);

        Map<String, Requirement> requirements = new HashMap<>();
        requirements.put(Constants.Requirements.PERMISSION.getFullKey(), createTestRequirement());
        requirements.put(Constants.Requirements.SERVER.getFullKey(), createTestRequirement());

        RequirementGroup group = RequirementGroup.builder()
                .operator(RequirementOperatorType.AND)
                .groups(requirements)
                .build();

        // When
        boolean result = evaluator.check(group, mockPlayer);

        // Then
        assertTrue(result);
    }

    @Test
    @DisplayName("AND operator - should return false when any requirement not met")
    void testAndOperatorNotAllMet() {
        // Given
        RequirementValidation alwaysTrueValidator = (req, player) -> RequirementResult.passed();
        RequirementValidation alwaysFalseValidator = (req, player) -> RequirementResult.denied();
        registry.register(Constants.Requirements.PERMISSION, alwaysTrueValidator);
        registry.register(Constants.Requirements.SERVER, alwaysFalseValidator);

        Map<String, Requirement> requirements = new HashMap<>();
        requirements.put(Constants.Requirements.PERMISSION.getFullKey(), createTestRequirement());
        requirements.put(Constants.Requirements.SERVER.getFullKey(), createTestRequirement());

        RequirementGroup group = RequirementGroup.builder()
                .operator(RequirementOperatorType.AND)
                .groups(requirements)
                .build();

        // When
        boolean result = evaluator.check(group, mockPlayer);

        // Then
        assertFalse(result);
    }

    @Test
    @DisplayName("OR operator - should return true when at least one requirement met")
    void testOrOperatorOneMet() {
        // Given
        RequirementValidation alwaysTrueValidator = (req, player) -> RequirementResult.passed();
        RequirementValidation alwaysFalseValidator = (req, player) -> RequirementResult.denied();
        registry.register(Constants.Requirements.PERMISSION, alwaysTrueValidator);
        registry.register(Constants.Requirements.SERVER, alwaysFalseValidator);

        Map<String, Requirement> requirements = new HashMap<>();
        requirements.put(Constants.Requirements.PERMISSION.getFullKey(), createTestRequirement());
        requirements.put(Constants.Requirements.SERVER.getFullKey(), createTestRequirement());

        RequirementGroup group = RequirementGroup.builder()
                .operator(RequirementOperatorType.OR)
                .groups(requirements)
                .build();

        // When
        boolean result = evaluator.check(group, mockPlayer);

        // Then
        assertTrue(result);
    }

    @Test
    @DisplayName("OR operator - should return false when no requirement met")
    void testOrOperatorNoneMet() {
        // Given
        RequirementValidation alwaysFalseValidator = (req, player) -> RequirementResult.denied();
        registry.register(Constants.Requirements.PERMISSION, alwaysFalseValidator);
        registry.register(Constants.Requirements.SERVER, alwaysFalseValidator);

        Map<String, Requirement> requirements = new HashMap<>();
        requirements.put(Constants.Requirements.PERMISSION.getFullKey(), createTestRequirement());
        requirements.put(Constants.Requirements.SERVER.getFullKey(), createTestRequirement());

        RequirementGroup group = RequirementGroup.builder()
                .operator(RequirementOperatorType.OR)
                .groups(requirements)
                .build();

        // When
        boolean result = evaluator.check(group, mockPlayer);

        // Then
        assertFalse(result);
    }

    @Test
    @DisplayName("XOR operator - should return true when exactly one requirement met")
    void testXorOperatorExactlyOneMet() {
        // Given
        RequirementValidation alwaysTrueValidator = (req, player) -> RequirementResult.passed();
        RequirementValidation alwaysFalseValidator = (req, player) -> RequirementResult.denied();
        registry.register(Constants.Requirements.PERMISSION, alwaysTrueValidator);
        registry.register(Constants.Requirements.SERVER, alwaysFalseValidator);

        Map<String, Requirement> requirements = new HashMap<>();
        requirements.put(Constants.Requirements.PERMISSION.getFullKey(), createTestRequirement());
        requirements.put(Constants.Requirements.SERVER.getFullKey(), createTestRequirement());

        RequirementGroup group = RequirementGroup.builder()
                .operator(RequirementOperatorType.XOR)
                .groups(requirements)
                .build();

        // When
        boolean result = evaluator.check(group, mockPlayer);

        // Then
        assertTrue(result);
    }

    @Test
    @DisplayName("NOT operator - should return true when no requirement met")
    void testNotOperatorNoneMet() {
        // Given
        RequirementValidation alwaysFalseValidator = (req, player) -> RequirementResult.denied();
        registry.register(Constants.Requirements.PERMISSION, alwaysFalseValidator);

        Map<String, Requirement> requirements = new HashMap<>();
        requirements.put(Constants.Requirements.PERMISSION.getFullKey(), createTestRequirement());

        RequirementGroup group = RequirementGroup.builder()
                .operator(RequirementOperatorType.NOT)
                .groups(requirements)
                .build();

        // When
        boolean result = evaluator.check(group, mockPlayer);

        // Then
        assertTrue(result);
    }

    @Test
    @DisplayName("NOT operator - should return false when any requirement met")
    void testNotOperatorAnyMet() {
        // Given
        RequirementValidation alwaysTrueValidator = (req, player) -> RequirementResult.passed();
        registry.register(Constants.Requirements.PERMISSION, alwaysTrueValidator);

        Map<String, Requirement> requirements = new HashMap<>();
        requirements.put(Constants.Requirements.PERMISSION.getFullKey(), createTestRequirement());

        RequirementGroup group = RequirementGroup.builder()
                .operator(RequirementOperatorType.NOT)
                .groups(requirements)
                .build();

        // When
        boolean result = evaluator.check(group, mockPlayer);

        // Then
        assertFalse(result);
    }

    @Test
    @DisplayName("AND operator - should be denied with the denial of its first unmet requirement")
    void testAndOperatorDenial() {
        register("first", RequirementResult.passed());
        register("second", deniedBy("second"));
        register("third", deniedBy("third"));

        RequirementResult result = evaluator.evaluate(group(RequirementOperatorType.AND, "first", "second", "third"), mockPlayer);

        assertTrue(result.isDenied());
        assertEquals("test:second", result.getDenial().getKey());
    }

    @Test
    @DisplayName("OR operator - should be denied with the first denial any requirement gave")
    void testOrOperatorDenial() {
        register("first", RequirementResult.denied());
        register("second", deniedBy("second"));
        register("third", deniedBy("third"));

        RequirementResult result = evaluator.evaluate(group(RequirementOperatorType.OR, "first", "second", "third"), mockPlayer);

        assertTrue(result.isDenied());
        assertEquals("test:second", result.getDenial().getKey());
    }

    @Test
    @DisplayName("OR operator - should pass without a denial when one requirement is met")
    void testOrOperatorPassesWithoutDenial() {
        register("first", deniedBy("first"));
        register("second", RequirementResult.passed());

        RequirementResult result = evaluator.evaluate(group(RequirementOperatorType.OR, "first", "second"), mockPlayer);

        assertTrue(result.isPassed());
        assertNull(result.getDenial());
    }

    @Test
    @DisplayName("XOR operator - should give the first denial when none is met and none when several are")
    void testXorOperatorDenials() {
        register("first", deniedBy("first"));
        register("second", deniedBy("second"));
        register("met", RequirementResult.passed());
        register("alsoMet", RequirementResult.passed());

        RequirementResult none = evaluator.evaluate(group(RequirementOperatorType.XOR, "first", "second"), mockPlayer);
        RequirementResult one = evaluator.evaluate(group(RequirementOperatorType.XOR, "first", "met"), mockPlayer);
        RequirementResult several = evaluator.evaluate(group(RequirementOperatorType.XOR, "met", "first", "alsoMet"), mockPlayer);

        assertEquals("test:first", none.getDenial().getKey());
        assertTrue(one.isPassed());
        assertTrue(several.isDenied());
        assertNull(several.getDenial());
    }

    @Test
    @DisplayName("NOT, NAND and NOR operators - should be denied without a denial, since requirements were met")
    void testInvertedOperatorsGiveNoDenial() {
        register("met", RequirementResult.passed());

        for (RequirementOperatorType operator : List.of(RequirementOperatorType.NOT, RequirementOperatorType.NAND, RequirementOperatorType.NOR)) {
            RequirementResult result = evaluator.evaluate(group(operator, "met"), mockPlayer);

            assertTrue(result.isDenied(), operator.name());
            assertNull(result.getDenial(), operator.name());
        }
    }

    @Test
    @DisplayName("Group reason - should be carried beside the denial of its requirements")
    void testGroupReasonBesideDenial() {
        register("world", deniedBy("world"));
        RequirementGroup group = group(RequirementOperatorType.AND, "world").toBuilder()
                .reason("Staff chat lives in {worlds}")
                .build();

        RequirementResult result = evaluator.evaluate(group, mockPlayer);

        assertEquals("Staff chat lives in {worlds}", result.getReason());
        assertEquals("test:world", result.getDenial().getKey());
    }

    @Test
    @DisplayName("Group reason - should explain a group whose requirements cannot")
    void testGroupReasonForInvertedGroup() {
        register("met", RequirementResult.passed());
        RequirementGroup group = group(RequirementOperatorType.NOT, "met").toBuilder()
                .reason("Not for you")
                .build();

        RequirementResult result = evaluator.evaluate(group, mockPlayer);

        assertEquals("Not for you", result.getReason());
        assertNull(result.getDenial());
    }

    @Test
    @DisplayName("Group reason - a blank one should drop the denial, and a passing group should keep passing")
    void testBlankGroupReason() {
        register("denied", deniedBy("denied"));
        register("met", RequirementResult.passed());

        RequirementResult silenced = evaluator.evaluate(group(RequirementOperatorType.AND, "denied").toBuilder().reason(" ").build(), mockPlayer);
        RequirementResult passed = evaluator.evaluate(group(RequirementOperatorType.AND, "met").toBuilder().reason("Never told").build(), mockPlayer);

        assertEquals(RequirementResult.denied(), silenced);
        assertTrue(passed.isPassed());
        assertNull(passed.getReason());
    }

    @Test
    @DisplayName("Module requirement - the example of RequirementValidation denies a muted player with its facts")
    void testModuleRequirementDenial() {
        MuteService mutes = uniqueId -> uniqueId.equals(MUTED_PLAYER) ? new Mute("12:30", "spam") : null;
        new MutedRequirementValidation(registry, mutes);
        RequirementGroup group = RequirementGroup.builder()
                .operator(RequirementOperatorType.AND)
                .groups(RequirementGroup.of(MutedRequirementValidation.MUTED, Requirement.builder()
                        .condition(RequirementConditionType.HAS)
                        .expected("false")
                        .build()))
                .build();
        SocialismusPlayer other = mock(SocialismusPlayer.class);
        when(mockPlayer.getUniqueId()).thenReturn(MUTED_PLAYER);
        when(other.getUniqueId()).thenReturn(UUID.randomUUID());

        RequirementResult muted = evaluator.evaluate(group, mockPlayer);
        RequirementResult free = evaluator.evaluate(group, other);

        assertEquals(RequirementDenial.of(
                MutedRequirementValidation.MUTED,
                RequirementFailure.FORBIDDEN,
                Map.of("until", "12:30", "reason", "spam")
        ).withMessage("<red>You are muted until {until}: {reason}"), muted.getDenial());
        assertTrue(free.isPassed());
    }

    private void register(String type, RequirementResult result) {
        registry.register(RequirementKey.create("test", type, Requirement.class), (requirement, player) -> result);
    }

    private static RequirementResult deniedBy(String type) {
        return RequirementResult.denied(RequirementDenial.of(
                RequirementKey.create("test", type, Requirement.class),
                RequirementFailure.REQUIRED,
                Map.of()
        ));
    }

    private RequirementGroup group(RequirementOperatorType operator, String... types) {
        Map<String, Requirement> requirements = new LinkedHashMap<>();
        for (String type : types)
            requirements.put("test:" + type, createTestRequirement());

        return RequirementGroup.builder()
                .operator(operator)
                .groups(requirements)
                .build();
    }

    private static final UUID MUTED_PLAYER = UUID.randomUUID();

    private record Mute(String until, String reason) {}

    private interface MuteService {
        Mute find(UUID uniqueId);
    }

    /**
     * The example of {@link RequirementValidation}'s Javadoc, as it is written there but without Guice.
     */
    private static class MutedRequirementValidation implements RequirementValidation {
        public static final RequirementKey<Requirement> MUTED =
                RequirementKey.create("litebans", "muted", Requirement.class);

        private final MuteService mutes;

        public MutedRequirementValidation(
                ExtendedRegistry<RequirementKey<?>, RequirementValidation> registry,
                MuteService mutes
        ) {
            this.mutes = mutes;
            registry.register(MUTED, this);
        }

        @Override
        public @NotNull RequirementResult evaluate(@NotNull Requirement requirement, @NotNull SocialismusPlayer player) {
            Mute mute = mutes.find(player.getUniqueId());
            if (String.valueOf(mute != null).equals(requirement.getExpected()))
                return RequirementResult.passed();
            if (mute == null)
                return RequirementResult.denied(RequirementDenial.of(MUTED, RequirementFailure.REQUIRED, Map.of()));

            return RequirementResult.denied(RequirementDenial.of(
                    MUTED,
                    RequirementFailure.FORBIDDEN,
                    Map.of("until", mute.until(), "reason", mute.reason())
            ).withMessage("<red>You are muted until {until}: {reason}"));
        }
    }

    private Requirement createTestRequirement() {
        return Requirement.builder()
                .condition(RequirementConditionType.EQUALS)
                .expected("test")
                .build();
    }
}

