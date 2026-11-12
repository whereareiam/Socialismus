package me.whereareiam.socialismus.buildlogic.packaging

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import javax.inject.Inject

/**
 * The oldest Java a downgraded jar runs on, such as `11` for the Minecraft 1.16.5 servers.
 */
abstract class DowngradeExtension @Inject constructor(
        objects: ObjectFactory
) {
    val java: Property<Int> = objects.property(Int::class.java)
        .convention(11)

    /**
     * Paths in the jar left as they are, such as a downgrader the jar carries for its own use, which already runs on
     * [java] and must keep its own classes where it looks for them.
     */
    val untouched: ListProperty<String> = objects.listProperty(String::class.java)
        .convention(emptyList())
}
