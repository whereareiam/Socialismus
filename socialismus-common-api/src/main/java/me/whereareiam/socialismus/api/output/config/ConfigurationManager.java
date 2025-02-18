package me.whereareiam.socialismus.api.output.config;

import me.whereareiam.socialismus.api.output.DefaultConfig;
import me.whereareiam.socialismus.api.type.ConfigurationType;

/**
 * Interface for managing configuration serialization, deserialization, and templates
 * in the Socialismus plugin. This manager handles configuration type detection,
 * custom serializers/deserializers, and default configuration templates.
 */
public interface ConfigurationManager {
    /**
     * Gets the configuration type currently in use.
     *
     * @return the current configuration type
     */
    ConfigurationType getConfigurationType();

    /**
     * Adds a custom deserializer for a specific class type.
     *
     * @param clazz the class to register the deserializer for
     * @param deserializer the deserializer instance
     */
    void addDeserializer(Class<?> clazz, Object deserializer);

    /**
     * Adds a custom serializer for a specific class type.
     *
     * @param clazz the class to register the serializer for
     * @param serializer the serializer instance
     */
    void addSerializer(Class<?> clazz, Object serializer);

    /**
     * Registers a default configuration template for a specific class type.
     *
     * @param clazz the class type for the template
     * @param template the default configuration template
     */
    void addTemplate(Class<?> clazz, DefaultConfig<?> template);

    /**
     * Retrieves the default configuration template for a specific class type.
     *
     * @param clazz the class type to get the template for
     * @param <T> the type of configuration class
     * @return the default configuration template
     */
    <T> DefaultConfig<T> getTemplate(Class<T> clazz);
}