package me.whereareiam.socialismus.common.requirement;

import me.whereareiam.socialismus.Constants;
import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.logging.LoggingHelper;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.model.requirement.Requirement;
import me.whereareiam.socialismus.model.requirement.RequirementGroup;
import me.whereareiam.socialismus.model.requirement.type.PermissionRequirement;
import me.whereareiam.socialismus.service.requirement.RequirementValidation;
import me.whereareiam.socialismus.type.requirement.RequirementConditionType;
import me.whereareiam.socialismus.type.requirement.RequirementOperatorType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        RequirementValidation alwaysTrueValidator = (req, player) -> true;
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
        RequirementValidation alwaysTrueValidator = (req, player) -> true;
        RequirementValidation alwaysFalseValidator = (req, player) -> false;
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
        RequirementValidation alwaysTrueValidator = (req, player) -> true;
        RequirementValidation alwaysFalseValidator = (req, player) -> false;
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
        RequirementValidation alwaysFalseValidator = (req, player) -> false;
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
        RequirementValidation alwaysTrueValidator = (req, player) -> true;
        RequirementValidation alwaysFalseValidator = (req, player) -> false;
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
        RequirementValidation alwaysFalseValidator = (req, player) -> false;
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
        RequirementValidation alwaysTrueValidator = (req, player) -> true;
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

    private Requirement createTestRequirement() {
        return Requirement.builder()
                .condition(RequirementConditionType.EQUALS)
                .expected("test")
                .build();
    }
}

