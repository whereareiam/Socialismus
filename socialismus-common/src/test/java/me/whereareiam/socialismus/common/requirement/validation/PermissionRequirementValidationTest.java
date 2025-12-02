package me.whereareiam.socialismus.common.requirement.validation;

import me.whereareiam.socialismus.common.requirement.RequirementRegistry;
import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.logging.LoggingHelper;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.model.requirement.Requirement;
import me.whereareiam.socialismus.model.requirement.type.PermissionRequirement;
import me.whereareiam.socialismus.type.requirement.RequirementConditionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("PermissionRequirementValidation Tests")
class PermissionRequirementValidationTest {

    private PermissionRequirementValidation validation;
    private RequirementRegistry registry;

    @Mock
    private SocialismusPlayer mockPlayer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Initialize Logger with a mock for testing
        Logger.init(mock(LoggingHelper.class));
        
        registry = new RequirementRegistry();
        validation = new PermissionRequirementValidation(registry);
        
        when(mockPlayer.getUsername()).thenReturn("TestPlayer");
    }

    @Test
    @DisplayName("Should return false for non-PermissionRequirement type")
    void testWrongRequirementType() {
        // Given
        Requirement genericRequirement = Requirement.builder()
                .condition(RequirementConditionType.HAS)
                .expected("true")
                .build();

        // When
        boolean result = validation.check(genericRequirement, mockPlayer);

        // Then
        assertFalse(result);
    }

    @Test
    @DisplayName("HAS condition - should return true when player has all permissions and expected is 'true'")
    void testHasConditionAllPermissionsTrue() {
        // Given
        PermissionRequirement requirement = PermissionRequirement.builder()
                .condition(RequirementConditionType.HAS)
                .expected("true")
                .permissions(List.of("permission.one", "permission.two"))
                .build();

        when(mockPlayer.hasPermission("permission.one")).thenReturn(true);
        when(mockPlayer.hasPermission("permission.two")).thenReturn(true);

        // When
        boolean result = validation.check(requirement, mockPlayer);

        // Then
        assertTrue(result);
        verify(mockPlayer).hasPermission("permission.one");
        verify(mockPlayer).hasPermission("permission.two");
    }

    @Test
    @DisplayName("HAS condition - should return true when player lacks permissions and expected is 'false'")
    void testHasConditionMissingPermissionsFalse() {
        // Given
        PermissionRequirement requirement = PermissionRequirement.builder()
                .condition(RequirementConditionType.HAS)
                .expected("false")
                .permissions(List.of("permission.one", "permission.two"))
                .build();

        when(mockPlayer.hasPermission("permission.one")).thenReturn(true);
        when(mockPlayer.hasPermission("permission.two")).thenReturn(false);

        // When
        boolean result = validation.check(requirement, mockPlayer);

        // Then
        assertTrue(result);
    }

    @Test
    @DisplayName("HAS condition - should return false when player lacks permissions and expected is 'true'")
    void testHasConditionMissingPermissionsTrue() {
        // Given
        PermissionRequirement requirement = PermissionRequirement.builder()
                .condition(RequirementConditionType.HAS)
                .expected("true")
                .permissions(List.of("permission.one", "permission.two"))
                .build();

        when(mockPlayer.hasPermission("permission.one")).thenReturn(true);
        when(mockPlayer.hasPermission("permission.two")).thenReturn(false);

        // When
        boolean result = validation.check(requirement, mockPlayer);

        // Then
        assertFalse(result);
    }

    @Test
    @DisplayName("CONTAINS condition - should return true when player has any permission and expected is 'true'")
    void testContainsConditionAnyPermissionTrue() {
        // Given
        PermissionRequirement requirement = PermissionRequirement.builder()
                .condition(RequirementConditionType.CONTAINS)
                .expected("true")
                .permissions(List.of("permission.one", "permission.two", "permission.three"))
                .build();

        when(mockPlayer.hasPermission("permission.one")).thenReturn(false);
        when(mockPlayer.hasPermission("permission.two")).thenReturn(true);
        when(mockPlayer.hasPermission("permission.three")).thenReturn(false);

        // When
        boolean result = validation.check(requirement, mockPlayer);

        // Then
        assertTrue(result);
    }

    @Test
    @DisplayName("CONTAINS condition - should return false when player has no permissions and expected is 'true'")
    void testContainsConditionNoPermissionsTrue() {
        // Given
        PermissionRequirement requirement = PermissionRequirement.builder()
                .condition(RequirementConditionType.CONTAINS)
                .expected("true")
                .permissions(List.of("permission.one", "permission.two"))
                .build();

        when(mockPlayer.hasPermission("permission.one")).thenReturn(false);
        when(mockPlayer.hasPermission("permission.two")).thenReturn(false);

        // When
        boolean result = validation.check(requirement, mockPlayer);

        // Then
        assertFalse(result);
    }

    @Test
    @DisplayName("CONTAINS condition - should return true when player has no permissions and expected is 'false'")
    void testContainsConditionNoPermissionsFalse() {
        // Given
        PermissionRequirement requirement = PermissionRequirement.builder()
                .condition(RequirementConditionType.CONTAINS)
                .expected("false")
                .permissions(List.of("permission.one", "permission.two"))
                .build();

        when(mockPlayer.hasPermission("permission.one")).thenReturn(false);
        when(mockPlayer.hasPermission("permission.two")).thenReturn(false);

        // When
        boolean result = validation.check(requirement, mockPlayer);

        // Then
        assertTrue(result);
    }

}

