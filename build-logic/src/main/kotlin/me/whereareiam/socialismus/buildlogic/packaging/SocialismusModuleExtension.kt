package me.whereareiam.socialismus.buildlogic.packaging

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import javax.inject.Inject

/**
 * The Socialismus module a module jar holds, such as `Chirper`; it names the jar and fills its `module.json`.
 */
abstract class SocialismusModuleExtension @Inject constructor(
        objects: ObjectFactory
) {
    val name: Property<String> = objects.property(String::class.java)
}
