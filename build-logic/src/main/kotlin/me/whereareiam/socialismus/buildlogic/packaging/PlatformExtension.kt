package me.whereareiam.socialismus.buildlogic.packaging

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import javax.inject.Inject

/**
 * The platform a plugin jar runs on, such as `PAPER`; it names the jar's classifier and its `Plugin-Type`.
 */
abstract class PlatformExtension @Inject constructor(
        objects: ObjectFactory
) {
    val name: Property<String> = objects.property(String::class.java)
}
