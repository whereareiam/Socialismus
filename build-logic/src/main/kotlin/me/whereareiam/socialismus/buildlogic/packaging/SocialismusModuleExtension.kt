package me.whereareiam.socialismus.buildlogic.packaging

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import javax.inject.Inject

/**
 * The Socialismus module a module jar holds, such as `Chirper`; it names the jar and fills its `module.json`.
 */
abstract class SocialismusModuleExtension @Inject constructor(
        objects: ObjectFactory
) {
    val name: Property<String> = objects.property(String::class.java)

    /**
     * The packages the modules depending on this one load from it, each with its subpackages, such as
     * `me.whereareiam.socialismus.module.database.api` and `org.jdbi`; written to `exports` in `module.json`. The
     * build fails when one has no class in the module or on its classpath.
     */
    val exports: ListProperty<String> = objects.listProperty(String::class.java).convention(emptyList())
}
