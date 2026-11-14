package me.whereareiam.socialismus.module.database.common.store;

import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.logging.LoggingHelper;
import me.whereareiam.socialismus.module.database.common.DefaultDatabaseService;
import me.whereareiam.socialismus.module.database.common.config.SqliteSettings;
import me.whereareiam.socialismus.store.StorePersistence;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class DatabaseStorePersistenceTest {
	@TempDir
	Path workingPath;

	@BeforeEach
	void silenceTheLog() {
		Logger.init(mock(LoggingHelper.class));
	}

	@Test
	void keepsTheValuesOfAStoreAcrossARestart_ApartFromTheOtherStores() throws Exception {
		try (DefaultDatabaseService database = DefaultDatabaseService.open(new SqliteSettings(), workingPath);
			 DatabaseStorePersistence persistence = database.storePersistence()) {
			persistence.save("mentionator:modes", "alice", new StorePersistence.Saved("\"DIRECT\"", null)).join();
			persistence.save("mentionator:modes", "bob", new StorePersistence.Saved("\"ALL\"", null)).join();
			persistence.save("mentionator:modes", "bob", new StorePersistence.Saved("\"NONE\"", null)).join();
			persistence.save("dialogue:ignores", "alice", new StorePersistence.Saved("[]", null)).join();
		}

		try (DefaultDatabaseService database = DefaultDatabaseService.open(new SqliteSettings(), workingPath);
			 DatabaseStorePersistence persistence = database.storePersistence()) {
			Map<String, StorePersistence.Saved> modes = persistence.load("mentionator:modes").join();

			assertEquals(Map.of(
					"alice", new StorePersistence.Saved("\"DIRECT\"", null),
					"bob", new StorePersistence.Saved("\"NONE\"", null)
			), modes);
		}
	}

	@Test
	void forgetsDeletedAndExpiredValues_AndKeepsTheExpiryOfTheOthers() throws Exception {
		Instant later = Instant.parse("2999-01-01T00:00:00.123456Z");
		try (DefaultDatabaseService database = DefaultDatabaseService.open(new SqliteSettings(), workingPath);
			 DatabaseStorePersistence persistence = database.storePersistence()) {
			persistence.save("socialismus:cooldowns", "gone", new StorePersistence.Saved("1", Instant.now().minusSeconds(5))).join();
			persistence.save("socialismus:cooldowns", "deleted", new StorePersistence.Saved("2", null)).join();
			persistence.save("socialismus:cooldowns", "kept", new StorePersistence.Saved("3", later)).join();
			persistence.delete("socialismus:cooldowns", "deleted").join();

			Map<String, StorePersistence.Saved> cooldowns = persistence.load("socialismus:cooldowns").join();

			assertEquals(Map.of("kept", new StorePersistence.Saved("3", later)), cooldowns);
		}
	}
}
