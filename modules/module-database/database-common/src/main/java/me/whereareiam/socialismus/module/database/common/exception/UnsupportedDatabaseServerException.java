package me.whereareiam.socialismus.module.database.common.exception;

import org.jetbrains.annotations.NotNull;

/**
 * The configured database server is older than SocialismusDatabase supports.
 */
public class UnsupportedDatabaseServerException extends RuntimeException {
	public UnsupportedDatabaseServerException(@NotNull String message) {
		super(message);
	}
}
