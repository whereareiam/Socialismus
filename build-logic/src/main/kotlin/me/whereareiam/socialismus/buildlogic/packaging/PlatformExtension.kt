package me.whereareiam.socialismus.buildlogic.packaging

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import javax.inject.Inject

/**
 * The platform a plugin jar runs on, such as `PAPER`; it names the jar's classifier and its `Plugin-Type`.
 */
abstract class PlatformExtension @Inject constructor(
        objects: ObjectFactory
) {
    val name: Property<String> = objects.property(String::class.java)

    /** Whether the bundle jar carries this platform; a platform whose shared classes differ stays out. */
    val bundled: Property<Boolean> = objects.property(Boolean::class.java)
        .convention(true)

    /** The descriptor files the platform reads, such as `paper-plugin.yml`, which the bundle jar carries. */
    val descriptors: ListProperty<String> = objects.listProperty(String::class.java)
        .convention(emptyList())
}
