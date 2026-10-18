package me.whereareiam.socialismus.module.database.common.schema;

import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.module.database.api.schema.DatabaseSchema;
import me.whereareiam.socialismus.module.database.api.schema.SchemaInstaller;
import me.whereareiam.strata.MigrationStream;
import me.whereareiam.strata.Strata;
import me.whereareiam.strata.adapter.database.DatabaseContext;
import me.whereareiam.strata.adapter.database.DatabaseTarget;
import me.whereareiam.strata.adapter.database.dialectica.SchemaCreation;
import me.whereareiam.strata.adapter.database.jdbi.JdbiConnector;
import me.whereareiam.strata.exception.MigrationException;
import me.whereareiam.strata.model.AppliedMigration;
import me.whereareiam.strata.model.MigrationReport;
import org.jdbi.v3.core.Jdbi;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Installs each owner's {@link DatabaseSchema} as a Strata migration stream on the database's Jdbi, and remembers
 * which streams this run has installed, so that a schema cannot be installed before the streams it requires.
 */
public final class DatabaseSchemaInstaller implements SchemaInstaller {
	private final Jdbi jdbi;
	private final Set<String> installed = new LinkedHashSet<>();

	/**
	 * @param jdbi the database's Jdbi; migrations run on its handles, so they see its plugins and mappers
	 */
	public DatabaseSchemaInstaller(@NotNull Jdbi jdbi) {
		this.jdbi = Objects.requireNonNull(jdbi, "jdbi");
	}

	@Override
	public synchronized void install(@NotNull DatabaseSchema schema) {
		String stream = schema.stream();
		Set<String> missing = new LinkedHashSet<>(schema.requires());
		missing.removeAll(installed);
		if (!missing.isEmpty())
			throw new IllegalStateException("Schema " + stream + " requires " + String.join(", ", missing)
					+ ", which has to be installed before it");

		MigrationStream<DatabaseContext> migrations = MigrationStream.<DatabaseContext>builder()
				.id(stream)
				.target(new DatabaseTarget(new JdbiConnector(jdbi)))
				.baseline(schema::baseline)
				.creation(SchemaCreation.of(schema.schema()))
				.migrations(schema.migrations())
				.build();
		MigrationReport report;
		try {
			report = new Strata(List.of(migrations)).migrate();
		} catch (MigrationException failure) {
			// Strata names the stream; the reason, such as a database a newer build wrote, is its cause.
			Logger.severe("Database schema %s could not be brought up to date: %s", stream, reason(failure));
			throw failure;
		}
		for (AppliedMigration applied : report.getApplied().getOrDefault(stream, List.of()))
			Logger.info("Database schema %s: applied %d %s", stream, applied.getVersion(), applied.getName());

		installed.add(stream);
	}

	private static String reason(Throwable failure) {
		Throwable reason = failure;
		while (reason.getCause() != null) reason = reason.getCause();

		return reason.getMessage() != null ? reason.getMessage() : reason.toString();
	}
}
