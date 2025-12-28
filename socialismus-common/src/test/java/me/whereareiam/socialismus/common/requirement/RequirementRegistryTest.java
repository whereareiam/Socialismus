package me.whereareiam.socialismus.common.requirement;

import me.whereareiam.socialismus.Constants;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.model.requirement.Requirement;
import me.whereareiam.socialismus.service.requirement.RequirementValidation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("RequirementRegistry Tests")
class RequirementRegistryTest {

    private RequirementRegistry registry;
    private RequirementValidation mockValidation;

    @BeforeEach
    void setUp() {
        registry = new RequirementRegistry();
        mockValidation = (requirement, player) -> true;
    }

    @Test
    @DisplayName("Should register validation successfully")
    void testRegister() {
        // When
        registry.register(Constants.Requirements.PERMISSION, mockValidation);

        // Then
        assertEquals(mockValidation, registry.get(Constants.Requirements.PERMISSION));
    }

    @Test
    @DisplayName("Should override existing registration")
    void testOverrideRegistration() {
        // Given
        RequirementValidation firstValidation = mockValidation;
        RequirementValidation secondValidation = (requirement, player) -> false;

        // When
        registry.register(Constants.Requirements.PERMISSION, firstValidation);
        registry.register(Constants.Requirements.PERMISSION, secondValidation);

        // Then
        assertEquals(secondValidation, registry.get(Constants.Requirements.PERMISSION));
        assertNotEquals(firstValidation, registry.get(Constants.Requirements.PERMISSION));
    }

    @Test
    @DisplayName("Should handle multiple requirement types")
    void testMultipleTypes() {
        // Given
        RequirementValidation permValidation = mockValidation;
        RequirementValidation serverValidation = (requirement, player) -> false;

        // When
        registry.register(Constants.Requirements.PERMISSION, permValidation);
        registry.register(Constants.Requirements.SERVER, serverValidation);

        // Then
        assertEquals(permValidation, registry.get(Constants.Requirements.PERMISSION));
        assertEquals(serverValidation, registry.get(Constants.Requirements.SERVER));
        assertNotEquals(registry.get(Constants.Requirements.PERMISSION), registry.get(Constants.Requirements.SERVER));
    }

    @Test
    @DisplayName("Should find validation by short key (no namespace)")
    void testShortKeyLookup() {
        // Given
        registry.register(Constants.Requirements.PERMISSION, mockValidation);

        // When & Then - all these should find the same validation
        assertEquals(mockValidation, registry.get("socialismus:permission"));
        assertEquals(mockValidation, registry.get("permission"));
        assertEquals(mockValidation, registry.get("PERMISSION"));
        assertEquals(mockValidation, registry.get("Permission"));
    }

    @Test
    @DisplayName("Should support external module namespaces")
    void testExternalModuleNamespace() {
        // Given - simulate an external module like Bubbler
        RequirementValidation bubblerValidation = (requirement, player) -> false;
        registry.register(
            me.whereareiam.socialismus.model.requirement.RequirementKey.create("bubbler", "activator", Requirement.class),
            bubblerValidation
        );

        // When & Then - should find by full key or short key
        assertEquals(bubblerValidation, registry.get("bubbler:activator"));
        assertEquals(bubblerValidation, registry.get("activator"));
        assertEquals(bubblerValidation, registry.get("ACTIVATOR"));
    }

    @Test
    @DisplayName("Should return null for non-existent key")
    void testNonExistentKey() {
        // When & Then
        assertNull(registry.get("nonexistent:key"));
        assertNull(registry.get("nonexistent"));
    }
}

