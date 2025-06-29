package me.whereareiam.socialismus.api.output.resource;

import me.whereareiam.socialismus.api.type.ResourceType;

import java.util.Map;

public interface ResourceProvider {
	/**
	 * map of ResourceType → implementation instance
	 */
	Map<ResourceType, ?> provideResources();
}
