package me.whereareiam.socialismus.common;

import me.whereareiam.configura.Configura;
import me.whereareiam.socialismus.model.requirement.Requirement;
import me.whereareiam.socialismus.model.requirement.RequirementGroup;
import me.whereareiam.socialismus.model.requirement.type.WorldRequirement;
import me.whereareiam.socialismus.type.requirement.RequirementConditionType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;

class CommonConfigurationTest {
	@TempDir
	Path dataPath;

	@Test
	void readsTheRequirementOfAModuleBesideTheBuiltInOnesWithTheGroupsReason() {
		Configura configura = new CommonConfiguration(dataPath).provideConfigura(dataPath);
		String yaml = """
				operator: AND
				reason: "<red>Not here, {worlds}"
				groups:
				  socialismus:world:
				    condition: CONTAINS
				    expected: "true"
				    worlds: [world]
				  litebans:muted:
				    condition: HAS
				    expected: "false"
				""";

		RequirementGroup group = configura.read(yaml.getBytes(StandardCharsets.UTF_8), RequirementGroup.class);

		assertEquals("<red>Not here, {worlds}", group.getReason());
		assertEquals(List.of("socialismus:world", "litebans:muted"), List.copyOf(group.getGroups().keySet()));
		WorldRequirement world = assertInstanceOf(WorldRequirement.class, group.getGroups().get("socialismus:world"));
		assertEquals(List.of("world"), world.getWorlds());
		Requirement muted = group.getGroups().get("litebans:muted");
		assertSame(Requirement.class, muted.getClass());
		assertEquals(RequirementConditionType.HAS, muted.getCondition());
		assertEquals("false", muted.getExpected());
	}
}
