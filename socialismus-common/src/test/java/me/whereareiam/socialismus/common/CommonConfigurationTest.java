package me.whereareiam.socialismus.common;

import me.whereareiam.conditia.model.Requirements;
import me.whereareiam.configura.Configura;
import me.whereareiam.socialismus.model.chat.Chat;
import me.whereareiam.socialismus.model.requirement.RequirementLists;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CommonConfigurationTest {
	@TempDir
	Path dataPath;

	@Test
	void readsTheRequirementListsOfAChatAsWritten() {
		Configura configura = new CommonConfiguration(dataPath).provideConfigura(dataPath);
		String yaml = """
				id: local
				requires:
				  - permission: socialismus.chat.local
				reaches:
				  - sender:
				      - not: { world: world_nether }
				""";

		Chat chat = configura.read(yaml.getBytes(StandardCharsets.UTF_8), Chat.class);

		assertEquals(RequirementLists.permission("socialismus.chat.local"), chat.getRequires());
		assertEquals(1, chat.getReaches().getConditions().items().size());
	}

	@Test
	void readsAChatWithoutRequirementsAsMetByEveryone() {
		Configura configura = new CommonConfiguration(dataPath).provideConfigura(dataPath);

		Chat chat = configura.read("id: fallback".getBytes(StandardCharsets.UTF_8), Chat.class);

		assertEquals(Requirements.none(), chat.getRequires());
		assertEquals(Requirements.none(), chat.getReaches());
	}
}
