package me.whereareiam.socialismus.integration;

import me.whereareiam.keystone.serializer.MessageDecorator;
import me.whereareiam.keystone.serializer.SerializerEngine;

/**
 * Integration interface for plugins that contribute decorators to the serialization pipeline.
 * Implementations of this interface can register their own {@link MessageDecorator}
 * with the SerializerEngine to modify messages during serialization.
 * <p>
 * Each integration controls its own decorator lifecycle and availability checking.
 * The decorator is only registered if {@link #isAvailable()} returns true.
 */
public interface SerializerIntegration extends Integration {
	/**
	 * Registers this integration's decorator with the serializer engine.
	 * This method is only called if {@link #isAvailable()} returns true.
	 *
	 * @param engine The serializer engine to register the decorator with
	 */
	void registerDecorator(SerializerEngine engine);
}