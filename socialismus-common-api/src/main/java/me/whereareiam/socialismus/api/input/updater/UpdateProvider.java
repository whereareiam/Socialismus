package me.whereareiam.socialismus.api.input.updater;

import me.whereareiam.socialismus.api.model.module.UpdateSpecification;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface UpdateProvider {
	/**
	 * @return the newest version string for this module OR empty() if
	 * the provider is temporarily unreachable.
	 */
	Optional<String> fetchLatest(UpdateSpecification.Spec spec) throws IOException;

	/**
	 * Fetches the identifiers of the most recent updates
	 * (e.g. commit SHAs, pre-release tags, whatever counts as “updates”)
	 * up to the given limit.
	 */
	List<String> fetchRecentUpdates(UpdateSpecification.Spec spec, int limit) throws IOException;
}
