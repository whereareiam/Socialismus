package me.whereareiam.socialismus.module.database.common.store;

import me.whereareiam.dialectica.Dialectica;
import me.whereareiam.dialectica.schema.Schema;
import me.whereareiam.socialismus.module.database.api.schema.DatabaseSchema;
import org.jetbrains.annotations.NotNull;

/**
 * The table of Socialismus's persistent stores, which SocialismusDatabase installs for them.
 */
public final class StoreSchema implements DatabaseSchema {
	@Override
	public @NotNull String stream() {
		return "socialismus/stores";
	}

	@Override
	public @NotNull Schema schema() {
		return Schema.of(Dialectica.entity(StoreEntryEntity.class).table());
	}
}
