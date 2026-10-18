package me.whereareiam.socialismus.common.requirement.validation;

import me.whereareiam.socialismus.Constants;
import me.whereareiam.socialismus.common.requirement.RequirementRegistry;
import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.logging.LoggingHelper;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.model.requirement.Requirement;
import me.whereareiam.socialismus.model.requirement.RequirementDenial;
import me.whereareiam.socialismus.model.requirement.RequirementResult;
import me.whereareiam.socialismus.model.requirement.type.ServerRequirement;
import me.whereareiam.socialismus.type.requirement.RequirementConditionType;
import me.whereareiam.socialismus.type.requirement.RequirementFailure;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("ServerRequirementValidation Tests")
class ServerRequirementValidationTest {
    private ServerRequirementValidation validation;

	@Mock
    private SocialismusPlayer mockPlayer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Initialize Logger with a mock for testing
        Logger.init(mock(LoggingHelper.class));
        
        RequirementRegistry registry = new RequirementRegistry();
        validation = new ServerRequirementValidation(registry);
        
        when(mockPlayer.getUsername()).thenReturn("TestPlayer");
    }

    @Test
    @DisplayName("Should return false for non-ServerRequirement type")
    void testWrongRequirementType() {
        // Given
        Requirement genericRequirement = Requirement.builder()
                .condition(RequirementConditionType.EQUALS)
                .expected("true")
                .build();

        when(mockPlayer.getServer()).thenReturn("lobby");

        // When
        boolean result = validation.evaluate(genericRequirement, mockPlayer).isPassed();

        // Then
        assertFalse(result);
    }

    @Test
    @DisplayName("Should return false when player server is null")
    void testNullPlayerServer() {
        // Given
        ServerRequirement requirement = ServerRequirement.builder()
                .condition(RequirementConditionType.EQUALS)
                .expected("true")
                .servers(List.of("lobby"))
                .build();

        when(mockPlayer.getServer()).thenReturn(null);

        // When
        boolean result = validation.evaluate(requirement, mockPlayer).isPassed();

        // Then
        assertFalse(result);
    }

    @Test
    @DisplayName("EQUALS condition - should return true when single server matches and expected is 'true'")
    void testEqualsConditionSingleServerMatchesTrue() {
        // Given
        ServerRequirement requirement = ServerRequirement.builder()
                .condition(RequirementConditionType.EQUALS)
                .expected("true")
                .servers(List.of("lobby"))
                .build();

        when(mockPlayer.getServer()).thenReturn("lobby");

        // When
        boolean result = validation.evaluate(requirement, mockPlayer).isPassed();

        // Then
        assertTrue(result);
        verify(mockPlayer, atLeastOnce()).getServer();
    }

    @Test
    @DisplayName("EQUALS condition - should return false when multiple servers in list")
    void testEqualsConditionMultipleServers() {
        // Given
        ServerRequirement requirement = ServerRequirement.builder()
                .condition(RequirementConditionType.EQUALS)
                .expected("true")
                .servers(List.of("lobby", "hub"))
                .build();

        when(mockPlayer.getServer()).thenReturn("lobby");

        // When
        boolean result = validation.evaluate(requirement, mockPlayer).isPassed();

        // Then
        assertFalse(result);
    }

    @Test
    @DisplayName("EQUALS condition - should return false when single server doesn't match")
    void testEqualsConditionSingleServerNoMatch() {
        // Given
        ServerRequirement requirement = ServerRequirement.builder()
                .condition(RequirementConditionType.EQUALS)
                .expected("true")
                .servers(List.of("lobby"))
                .build();

        when(mockPlayer.getServer()).thenReturn("hub");

        // When
        boolean result = validation.evaluate(requirement, mockPlayer).isPassed();

        // Then
        assertFalse(result);
    }

    @Test
    @DisplayName("EQUALS condition - should return true when server doesn't match and expected is 'false'")
    void testEqualsConditionNoMatchExpectedFalse() {
        // Given
        ServerRequirement requirement = ServerRequirement.builder()
                .condition(RequirementConditionType.EQUALS)
                .expected("false")
                .servers(List.of("lobby"))
                .build();

        when(mockPlayer.getServer()).thenReturn("hub");

        // When
        boolean result = validation.evaluate(requirement, mockPlayer).isPassed();

        // Then
        assertTrue(result);
    }

    @Test
    @DisplayName("CONTAINS condition - should return true when server is in list and expected is 'true'")
    void testContainsConditionServerInListTrue() {
        // Given
        ServerRequirement requirement = ServerRequirement.builder()
                .condition(RequirementConditionType.CONTAINS)
                .expected("true")
                .servers(List.of("lobby", "hub", "survival"))
                .build();

        when(mockPlayer.getServer()).thenReturn("hub");

        // When
        boolean result = validation.evaluate(requirement, mockPlayer).isPassed();

        // Then
        assertTrue(result);
    }

    @Test
    @DisplayName("CONTAINS condition - should return false when server not in list and expected is 'true'")
    void testContainsConditionServerNotInListTrue() {
        // Given
        ServerRequirement requirement = ServerRequirement.builder()
                .condition(RequirementConditionType.CONTAINS)
                .expected("true")
                .servers(List.of("lobby", "hub"))
                .build();

        when(mockPlayer.getServer()).thenReturn("survival");

        // When
        boolean result = validation.evaluate(requirement, mockPlayer).isPassed();

        // Then
        assertFalse(result);
    }

    @Test
    @DisplayName("CONTAINS condition - should return true when server not in list and expected is 'false'")
    void testContainsConditionServerNotInListFalse() {
        // Given
        ServerRequirement requirement = ServerRequirement.builder()
                .condition(RequirementConditionType.CONTAINS)
                .expected("false")
                .servers(List.of("lobby", "hub"))
                .build();

        when(mockPlayer.getServer()).thenReturn("survival");

        // When
        boolean result = validation.evaluate(requirement, mockPlayer).isPassed();

        // Then
        assertTrue(result);
    }

    @Test
    @DisplayName("Should handle multiple expected values separated by pipe")
    void testMultipleExpectedValues() {
        // Given
        ServerRequirement requirement = ServerRequirement.builder()
                .condition(RequirementConditionType.CONTAINS)
                .expected("true|false")
                .servers(List.of("lobby"))
                .build();

        when(mockPlayer.getServer()).thenReturn("hub");

        // When
        boolean result = validation.evaluate(requirement, mockPlayer).isPassed();

        // Then
        assertTrue(result); // Should match "false" from the expected values
    }


    @Test
    @DisplayName("Denial - a player on another server is denied as REQUIRED with the servers and their own")
    void testDenialOfOtherServer() {
        when(mockPlayer.getServer()).thenReturn("survival");
        ServerRequirement requirement = ServerRequirement.builder()
                .condition(RequirementConditionType.CONTAINS)
                .expected("true")
                .servers(List.of("lobby", "hub"))
                .build();

        RequirementResult result = validation.evaluate(requirement, mockPlayer);

        assertEquals(RequirementDenial.of(
                Constants.Requirements.SERVER,
                RequirementFailure.REQUIRED,
                Map.of("servers", "lobby, hub", "server", "survival")
        ), result.getDenial());
    }
}
