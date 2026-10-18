package me.whereareiam.socialismus.common.config.defaults.chat;

import me.whereareiam.configura.Config;
import me.whereareiam.configura.Configura;
import me.whereareiam.configura.feature.polymorphic.PolymorphicFeature;
import me.whereareiam.configura.type.Format;
import me.whereareiam.socialismus.model.chat.ChatSettings;
import me.whereareiam.socialismus.model.requirement.Requirement;
import me.whereareiam.socialismus.model.requirement.RequirementGroup;
import me.whereareiam.socialismus.model.requirement.type.PermissionRequirement;
import me.whereareiam.socialismus.model.requirement.type.WorldRequirement;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class ChatSettingsDefaultsTest {
	@TempDir
	Path dataPath;

	@Test
	void givesChatHistoryTheAdministratorPermissionWhereTheFileHasNoRequirements() throws IOException {
		Path file = write("history:\n  historySize: 500\n");

		ChatSettings.ChatHistorySettings history = configura().update(file, ChatSettings.class).getHistory();

		assertEquals(500, history.getHistorySize());
		assertEquals(List.of("socialismus.admin"), permissions(history.getRequirements()));
		assertEquals(List.of("socialismus.admin"), permissions(history.getBypassRequirements()));
	}

	@Test
	void keepsTheRequirementGroupsOfChatHistoryAsWritten() throws IOException {
		Path file = write("""
				history:
				  requirements:
				    operator: AND
				    groups:
				      socialismus:world:
				        condition: CONTAINS
				        expected: "true"
				        worlds: [world]
				""");

		ChatSettings.ChatHistorySettings history = configura().update(file, ChatSettings.class).getHistory();

		RequirementGroup requirements = history.getRequirements();
		assertEquals(List.of("socialismus:world"), List.copyOf(requirements.getGroups().keySet()));
		assertInstanceOf(WorldRequirement.class, requirements.getGroups().get("socialismus:world"));
		assertEquals(List.of("socialismus.admin"), permissions(history.getBypassRequirements()));
	}

	private static List<String> permissions(RequirementGroup group) {
		Requirement requirement = group.getGroups().get("socialismus:permission");

		return assertInstanceOf(PermissionRequirement.class, requirement).getPermissions();
	}

	private Configura configura() {
		PolymorphicFeature polymorphic = PolymorphicFeature.defaults();
		polymorphic.register(Requirement.class)
				.inferByField("worlds", WorldRequirement.class)
				.inferByField("permissions", PermissionRequirement.class)
				.build();

		return Config.builder()
				.format(Format.YAML)
				.feature(polymorphic)
				.build()
				.withDefaults(ChatSettingsDefaults.class);
	}

	private Path write(String content) throws IOException {
		Path file = dataPath.resolve("settings.yml");
		Files.writeString(file, content);

		return file;
	}
}
