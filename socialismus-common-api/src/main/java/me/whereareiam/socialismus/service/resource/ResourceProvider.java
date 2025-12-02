package me.whereareiam.socialismus.service.resource;

import me.whereareiam.socialismus.type.ResourceType;

import java.util.Map;

public interface ResourceProvider {
	/**
	 * map of ResourceType → implementation instance
	 */
	Map<ResourceType, ?> provideResources();
}
