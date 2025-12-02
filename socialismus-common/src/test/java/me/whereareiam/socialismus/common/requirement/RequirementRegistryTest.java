package me.whereareiam.socialismus.common.requirement;

import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.model.requirement.Requirement;
import me.whereareiam.socialismus.service.requirement.RequirementValidation;
import me.whereareiam.socialismus.type.requirement.RequirementType;
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
        mockValidation = new RequirementValidation() {
            @Override
            public boolean check(Requirement requirement, SocialismusPlayer player) {
                return true;
            }
        };
    }

    @Test
    @DisplayName("Should register validation successfully")
    void testRegister() {
        // When
        registry.register(RequirementType.PERMISSION, mockValidation);

        // Then
        assertEquals(mockValidation, registry.get(RequirementType.PERMISSION));
    }

    @Test
    @DisplayName("Should override existing registration")
    void testOverrideRegistration() {
        // Given
        RequirementValidation firstValidation = mockValidation;
        RequirementValidation secondValidation = new RequirementValidation() {
            @Override
            public boolean check(Requirement requirement, SocialismusPlayer player) {
                return false;
            }
        };

        // When
        registry.register(RequirementType.PERMISSION, firstValidation);
        registry.register(RequirementType.PERMISSION, secondValidation);

        // Then
        assertEquals(secondValidation, registry.get(RequirementType.PERMISSION));
        assertNotEquals(firstValidation, registry.get(RequirementType.PERMISSION));
    }

    @Test
    @DisplayName("Should handle multiple requirement types")
    void testMultipleTypes() {
        // Given
        RequirementValidation permValidation = mockValidation;
        RequirementValidation serverValidation = new RequirementValidation() {
            @Override
            public boolean check(Requirement requirement, SocialismusPlayer player) {
                return false;
            }
        };

        // When
        registry.register(RequirementType.PERMISSION, permValidation);
        registry.register(RequirementType.SERVER, serverValidation);

        // Then
        assertEquals(permValidation, registry.get(RequirementType.PERMISSION));
        assertEquals(serverValidation, registry.get(RequirementType.SERVER));
        assertNotEquals(registry.get(RequirementType.PERMISSION), registry.get(RequirementType.SERVER));
    }
}

