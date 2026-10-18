package me.whereareiam.socialismus.module.database.api.schema;

import me.whereareiam.strata.exception.MigrationException;
import org.jetbrains.annotations.NotNull;

/**
 * Installs the {@link DatabaseSchema} of an owner into Socialismus's database, or upgrades it. A module installs
 * its schema once the database is there, before it reads or writes its tables, whether the database was
 * registered before the module loaded or later:
 * <pre>{@code
 * database.getSchemaInstaller().install(new NotesSchema());
 * }</pre>
 */
public interface SchemaInstaller {
	/**
	 * Brings the schema's tables up to date: creates them on a new installation, or runs the migrations the
	 * database has not seen yet. Installing a schema again changes nothing.
	 *
	 * @param schema the schema to install
	 * @throws IllegalStateException when a stream the schema {@linkplain DatabaseSchema#requires() requires} has not
	 *                               been installed before it
	 * @throws MigrationException    when the schema could not be brought up to date, for example because a
	 *                               migration failed or a newer build wrote the database; the owner must not
	 *                               continue on that schema
	 */
	void install(@NotNull DatabaseSchema schema);
}
