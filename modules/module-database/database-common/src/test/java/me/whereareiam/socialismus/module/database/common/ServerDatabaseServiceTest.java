package me.whereareiam.socialismus.module.database.common;

import me.whereareiam.socialismus.module.database.common.config.DatabaseSettings;
import me.whereareiam.socialismus.module.database.common.config.MysqlSettings;
import me.whereareiam.socialismus.module.database.common.config.PostgresSettings;
import me.whereareiam.socialismus.module.database.common.config.ServerSettings;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.mariadb.MariaDBContainer;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.time.Duration;

/**
 * Runs the database contract on database servers in containers. Each run starts on a fresh server, so the restart
 * the contract checks is a new pool on the same server.
 */
@Tag("integration")
class ServerDatabaseServiceTest {
	private static final Duration STARTUP_TIMEOUT = Duration.ofMinutes(5);

	private static DatabaseSettings server(ServerSettings server, JdbcDatabaseContainer<?> container) {
		server.setHost(container.getHost());
		server.setPort(container.getFirstMappedPort());
		server.setDatabase(container.getDatabaseName());
		server.setUsername(container.getUsername());
		server.setPassword(container.getPassword());

		return server;
	}

	@Nested
	class Postgres extends DatabaseServiceContract {
		private static PostgreSQLContainer container;

		@BeforeAll
		static void start() {
			container = new PostgreSQLContainer("postgres:17-alpine").withStartupTimeout(STARTUP_TIMEOUT);
			container.start();
		}

		@AfterAll
		static void stop() {
			container.stop();
		}

		@Override
		protected DatabaseSettings settings() {
			return server(new PostgresSettings(), container);
		}
	}

	@Nested
	class MariaDb extends DatabaseServiceContract {
		private static MariaDBContainer container;

		@BeforeAll
		static void start() {
			container = new MariaDBContainer("mariadb:11").withStartupTimeoutSeconds((int) STARTUP_TIMEOUT.toSeconds());
			container.start();
		}

		@AfterAll
		static void stop() {
			container.stop();
		}

		@Override
		protected DatabaseSettings settings() {
			return server(new MysqlSettings(), container);
		}
	}
}
