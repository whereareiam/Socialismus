package me.whereareiam.socialismus.buildlogic.packaging

import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.SetProperty
import javax.inject.Inject

/**
 * The platforms the bundle jar carries; each bundled platform jar adds its project path.
 */
abstract class PlatformCollectionExtension @Inject constructor(
        objects: ObjectFactory
) {
    val memberPaths: SetProperty<String> = objects.setProperty(String::class.java)
        .convention(emptySet())

    companion object {
        /** The collection of the build, kept on the root project. */
        fun of(project: Project): PlatformCollectionExtension =
            project.rootProject.extensions.findByType(PlatformCollectionExtension::class.java)
                ?: project.rootProject.extensions.create("platformCollection", PlatformCollectionExtension::class.java)
    }
}
