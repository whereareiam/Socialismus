package me.whereareiam.socialismus.input.updater;

import me.whereareiam.socialismus.model.update.UpdateSource;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface UpdateProvider {
	/**
	 * @return the newest version string for this module OR empty() if
	 * the provider is temporarily unreachable.
	 */
	Optional<String> fetchLatest(UpdateSource source) throws IOException;

	/**
	 * Fetches the identifiers of the most recent updates
	 * (e.g. commit SHAs, pre-release tags, whatever counts as "updates")
	 * up to the given limit.
	 */
	List<String> fetchRecentUpdates(UpdateSource source, int limit) throws IOException;
}
