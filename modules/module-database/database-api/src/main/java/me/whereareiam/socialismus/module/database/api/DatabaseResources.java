package me.whereareiam.socialismus.module.database.api;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.whereareiam.socialismus.model.resource.ResourceKey;

/**
 * The resources SocialismusDatabase registers in Socialismus's resource registry.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DatabaseResources {
	/**
	 * The open database, registered while SocialismusDatabase is loaded.
	 */
	public static final ResourceKey<DatabaseService> DATABASE =
			ResourceKey.create("socialismusdatabase", "database", DatabaseService.class);
}
