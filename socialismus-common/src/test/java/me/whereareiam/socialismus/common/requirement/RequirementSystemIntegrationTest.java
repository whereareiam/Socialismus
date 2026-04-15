package me.whereareiam.socialismus.common.requirement;

import me.whereareiam.socialismus.Constants;
import me.whereareiam.socialismus.common.requirement.validation.PermissionRequirementValidation;
import me.whereareiam.socialismus.common.requirement.validation.ServerRequirementValidation;
import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.logging.LoggingHelper;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.model.requirement.Requirement;
import me.whereareiam.socialismus.model.requirement.RequirementGroup;
import me.whereareiam.socialismus.model.requirement.type.PermissionRequirement;
import me.whereareiam.socialismus.model.requirement.type.ServerRequirement;
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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("Requirements System Integration Tests")
class RequirementSystemIntegrationTest {
	private RequirementEvaluator evaluator;
    
    @Mock
    private SocialismusPlayer mockPlayer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Initialize Logger with a mock for testing
        Logger.init(mock(LoggingHelper.class));

	    RequirementRegistry registry = new RequirementRegistry();
        evaluator = new RequirementEvaluator(registry);
        
        // Register validations
        new PermissionRequirementValidation(registry);
        new ServerRequirementValidation(registry);
        
        when(mockPlayer.getUsername()).thenReturn("TestPlayer");
    }

    @Test
    @DisplayName("Integration: Player with permission and on correct server (AND)")
    void testPlayerMeetsAllRequirementsAnd() {
        // Given
        when(mockPlayer.hasPermission("chat.use")).thenReturn(true);
        when(mockPlayer.getServer()).thenReturn("lobby");

        Map<String, Requirement> requirements = new HashMap<>();
        requirements.put(Constants.Requirements.PERMISSION.getFullKey(), PermissionRequirement.builder()
                .condition(RequirementConditionType.HAS)
                .expected("true")
                .permissions(List.of("chat.use"))
                .build());
        requirements.put(Constants.Requirements.SERVER.getFullKey(), ServerRequirement.builder()
                .condition(RequirementConditionType.CONTAINS)
                .expected("true")
                .servers(List.of("lobby", "hub"))
                .build());

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
    @DisplayName("Integration: Player with permission but on wrong server (AND)")
    void testPlayerFailsServerRequirementAnd() {
        // Given
        when(mockPlayer.hasPermission("chat.use")).thenReturn(true);
        when(mockPlayer.getServer()).thenReturn("survival");

        Map<String, Requirement> requirements = new HashMap<>();
        requirements.put(Constants.Requirements.PERMISSION.getFullKey(), PermissionRequirement.builder()
                .condition(RequirementConditionType.HAS)
                .expected("true")
                .permissions(List.of("chat.use"))
                .build());
        requirements.put(Constants.Requirements.SERVER.getFullKey(), ServerRequirement.builder()
                .condition(RequirementConditionType.CONTAINS)
                .expected("true")
                .servers(List.of("lobby", "hub"))
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
    @DisplayName("Integration: Player meets at least one requirement (OR)")
    void testPlayerMeetsOneRequirementOr() {
        // Given
        when(mockPlayer.hasPermission("chat.use")).thenReturn(false);
        when(mockPlayer.getServer()).thenReturn("lobby");

        Map<String, Requirement> requirements = new HashMap<>();
        requirements.put(Constants.Requirements.PERMISSION.getFullKey(), PermissionRequirement.builder()
                .condition(RequirementConditionType.HAS)
                .expected("true")
                .permissions(List.of("chat.use"))
                .build());
        requirements.put(Constants.Requirements.SERVER.getFullKey(), ServerRequirement.builder()
                .condition(RequirementConditionType.CONTAINS)
                .expected("true")
                .servers(List.of("lobby", "hub"))
                .build());

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
    @DisplayName("Integration: Complex permission check with multiple permissions")
    void testComplexPermissionCheck() {
        // Given
        when(mockPlayer.hasPermission("chat.color")).thenReturn(true);
        when(mockPlayer.hasPermission("chat.format")).thenReturn(true);
        when(mockPlayer.hasPermission("chat.links")).thenReturn(true);

        Map<String, Requirement> requirements = new HashMap<>();
        requirements.put(Constants.Requirements.PERMISSION.getFullKey(), PermissionRequirement.builder()
                .condition(RequirementConditionType.HAS)
                .expected("true")
                .permissions(List.of("chat.color", "chat.format", "chat.links"))
                .build());

        RequirementGroup group = RequirementGroup.builder()
                .operator(RequirementOperatorType.AND)
                .groups(requirements)
                .build();

        // When
        boolean result = evaluator.check(group, mockPlayer);

        // Then
        assertTrue(result);
        verify(mockPlayer).hasPermission("chat.color");
        verify(mockPlayer).hasPermission("chat.format");
        verify(mockPlayer).hasPermission("chat.links");
    }

    @Test
    @DisplayName("Integration: Player has any of the permissions (CONTAINS)")
    void testPlayerHasAnyPermission() {
        // Given
        when(mockPlayer.hasPermission("chat.color")).thenReturn(false);
        when(mockPlayer.hasPermission("chat.format")).thenReturn(true);
        when(mockPlayer.hasPermission("chat.links")).thenReturn(false);

        Map<String, Requirement> requirements = new HashMap<>();
        requirements.put(Constants.Requirements.PERMISSION.getFullKey(), PermissionRequirement.builder()
                .condition(RequirementConditionType.CONTAINS)
                .expected("true")
                .permissions(List.of("chat.color", "chat.format", "chat.links"))
                .build());

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
    @DisplayName("Integration: XOR operator with exactly one requirement met")
    void testXorWithExactlyOneRequirementMet() {
        // Given
        when(mockPlayer.hasPermission("vip.chat")).thenReturn(true);
        when(mockPlayer.getServer()).thenReturn("survival");

        Map<String, Requirement> requirements = new HashMap<>();
        requirements.put(Constants.Requirements.PERMISSION.getFullKey(), PermissionRequirement.builder()
                .condition(RequirementConditionType.HAS)
                .expected("true")
                .permissions(List.of("vip.chat"))
                .build());
        requirements.put(Constants.Requirements.SERVER.getFullKey(), ServerRequirement.builder()
                .condition(RequirementConditionType.CONTAINS)
                .expected("true")
                .servers(List.of("lobby", "hub"))
                .build());

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
    @DisplayName("Integration: XOR operator fails when both requirements met")
    void testXorFailsWithBothRequirementsMet() {
        // Given
        when(mockPlayer.hasPermission("vip.chat")).thenReturn(true);
        when(mockPlayer.getServer()).thenReturn("lobby");

        Map<String, Requirement> requirements = new HashMap<>();
        requirements.put(Constants.Requirements.PERMISSION.getFullKey(), PermissionRequirement.builder()
                .condition(RequirementConditionType.HAS)
                .expected("true")
                .permissions(List.of("vip.chat"))
                .build());
        requirements.put(Constants.Requirements.SERVER.getFullKey(), ServerRequirement.builder()
                .condition(RequirementConditionType.CONTAINS)
                .expected("true")
                .servers(List.of("lobby", "hub"))
                .build());

        RequirementGroup group = RequirementGroup.builder()
                .operator(RequirementOperatorType.XOR)
                .groups(requirements)
                .build();

        // When
        boolean result = evaluator.check(group, mockPlayer);

        // Then
        assertFalse(result);
    }

    @Test
    @DisplayName("Integration: NOT operator inverts requirement result")
    void testNotOperatorInvertsResult() {
        // Given
        when(mockPlayer.hasPermission("banned.chat")).thenReturn(true);

        Map<String, Requirement> requirements = new HashMap<>();
        requirements.put(Constants.Requirements.PERMISSION.getFullKey(), PermissionRequirement.builder()
                .condition(RequirementConditionType.HAS)
                .expected("true")
                .permissions(List.of("banned.chat"))
                .build());

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
    @DisplayName("Integration: Server equals check with single server")
    void testServerEqualsWithSingleServer() {
        // Given
        when(mockPlayer.getServer()).thenReturn("lobby");

        Map<String, Requirement> requirements = new HashMap<>();
        requirements.put(Constants.Requirements.SERVER.getFullKey(), ServerRequirement.builder()
                .condition(RequirementConditionType.EQUALS)
                .expected("true")
                .servers(List.of("lobby"))
                .build());

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
    @DisplayName("Integration: Multiple expected values with pipe separator")
    void testMultipleExpectedValuesWithPipe() {
        // Given
        when(mockPlayer.getServer()).thenReturn("creative");

        Map<String, Requirement> requirements = new HashMap<>();
        requirements.put(Constants.Requirements.SERVER.getFullKey(), ServerRequirement.builder()
                .condition(RequirementConditionType.CONTAINS)
                .expected("true|false")
                .servers(List.of("lobby", "hub"))
                .build());

        RequirementGroup group = RequirementGroup.builder()
                .operator(RequirementOperatorType.AND)
                .groups(requirements)
                .build();

        // When
        boolean result = evaluator.check(group, mockPlayer);

        // Then
        assertTrue(result); // Should match "false" in expected values
    }

    @Test
    @DisplayName("Integration: Complex scenario with staff checking")
    void testComplexStaffScenario() {
        // Given - Staff must have permission AND be on management servers
        when(mockPlayer.hasPermission("staff.access")).thenReturn(true);
        when(mockPlayer.hasPermission("staff.moderate")).thenReturn(true);
        when(mockPlayer.getServer()).thenReturn("staff-lobby");

        Map<String, Requirement> requirements = new HashMap<>();
        requirements.put(Constants.Requirements.PERMISSION.getFullKey(), PermissionRequirement.builder()
                .condition(RequirementConditionType.HAS)
                .expected("true")
                .permissions(List.of("staff.access", "staff.moderate"))
                .build());
        requirements.put(Constants.Requirements.SERVER.getFullKey(), ServerRequirement.builder()
                .condition(RequirementConditionType.CONTAINS)
                .expected("true")
                .servers(List.of("staff-lobby", "admin-hub"))
                .build());

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
    @DisplayName("Integration: Blacklist scenario using NOT operator")
    void testBlacklistScenario() {
        // Given - Player should NOT be on blacklisted servers
        when(mockPlayer.getServer()).thenReturn("lobby");

        Map<String, Requirement> requirements = new HashMap<>();
        requirements.put(Constants.Requirements.SERVER.getFullKey(), ServerRequirement.builder()
                .condition(RequirementConditionType.CONTAINS)
                .expected("true")
                .servers(List.of("restricted", "banned", "quarantine"))
                .build());

        RequirementGroup group = RequirementGroup.builder()
                .operator(RequirementOperatorType.NOT)
                .groups(requirements)
                .build();

        // When
        boolean result = evaluator.check(group, mockPlayer);

        // Then
        assertTrue(result); // Player is NOT on blacklisted servers
    }
}

