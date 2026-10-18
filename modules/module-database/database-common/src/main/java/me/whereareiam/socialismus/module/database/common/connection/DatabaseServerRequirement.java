package me.whereareiam.socialismus.module.database.common.connection;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.whereareiam.socialismus.module.database.common.config.DatabaseType;
import me.whereareiam.socialismus.module.database.common.exception.UnsupportedDatabaseServerException;
import org.jetbrains.annotations.NotNull;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The oldest database servers SocialismusDatabase runs on.
 *
 * <p>Dialectica's MySQL dialect names the inserted row of an upsert with the row alias {@code excluded}, which MySQL
 * accepts from 8.0.19 on. MariaDB, configured as {@link DatabaseType#MYSQL} as well, versions itself apart from MySQL
 * and gets a dialect of its own that works on every release its driver reaches, so it has no minimum here; SQLite
 * and PostgreSQL have none either.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DatabaseServerRequirement {
	private static final String MYSQL = "MySQL";
	private static final ServerVersion MYSQL_MINIMUM = new ServerVersion(8, 0, 19);
	private static final Pattern VERSION = Pattern.compile("^(\\d+)\\.(\\d+)(?:\\.(\\d+))?");

	/**
	 * Checks the server a data source connects to.
	 *
	 * @param type       the configured database type
	 * @param dataSource the data source of the configured database
	 * @throws UnsupportedDatabaseServerException when the server is too old
	 * @throws SQLException                       when the server cannot be reached
	 */
	public static void verify(@NotNull DatabaseType type, @NotNull DataSource dataSource) throws SQLException {
		if (type != DatabaseType.MYSQL) return;

		try (Connection connection = dataSource.getConnection()) {
			DatabaseMetaData metaData = connection.getMetaData();
			verify(type, metaData.getDatabaseProductName(), metaData.getDatabaseProductVersion());
		}
	}

	/**
	 * Checks a server by the product name and version its driver reports.
	 *
	 * @param type           the configured database type
	 * @param productName    the server's product name, such as {@code MySQL} or {@code MariaDB}
	 * @param productVersion the server's version, such as {@code 8.0.36} or {@code 5.7.44-log}
	 * @throws UnsupportedDatabaseServerException when the server is MySQL older than 8.0.19, or MySQL with a version
	 *                                            that cannot be read
	 */
	public static void verify(@NotNull DatabaseType type, @NotNull String productName, @NotNull String productVersion) {
		if (type != DatabaseType.MYSQL || !MYSQL.equalsIgnoreCase(productName)) return;

		Matcher matcher = VERSION.matcher(productVersion.trim());
		if (!matcher.find())
			throw new UnsupportedDatabaseServerException("The database server reports MySQL version \"%s\", which cannot be read. SocialismusDatabase needs MySQL %s or newer."
					.formatted(productVersion, MYSQL_MINIMUM));

		ServerVersion found = new ServerVersion(
				Integer.parseInt(matcher.group(1)),
				Integer.parseInt(matcher.group(2)),
				matcher.group(3) == null ? 0 : Integer.parseInt(matcher.group(3))
		);
		if (found.compareTo(MYSQL_MINIMUM) < 0)
			throw new UnsupportedDatabaseServerException("The database server runs MySQL %s, but SocialismusDatabase needs MySQL %s or newer. Upgrade the MySQL server, or choose another database type."
					.formatted(productVersion, MYSQL_MINIMUM));
	}

	private record ServerVersion(int major, int minor, int patch) implements Comparable<ServerVersion> {
		@Override
		public int compareTo(@NotNull ServerVersion other) {
			if (major != other.major) return Integer.compare(major, other.major);
			if (minor != other.minor) return Integer.compare(minor, other.minor);

			return Integer.compare(patch, other.patch);
		}

		@Override
		public @NotNull String toString() {
			return major + "." + minor + "." + patch;
		}
	}
}
