import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import me.whereareiam.socialismus.buildlogic.packaging.PlatformCollectionExtension
import me.whereareiam.socialismus.buildlogic.packaging.PlatformExtension

plugins {
    id("packaging-shaded-jar")
}

val platformCollection = PlatformCollectionExtension.of(project)

// The bundle carries platforms built for different Java versions; each server only loads its own.
java {
    disableAutoTargetJvm()
}

gradle.projectsEvaluated {
    val memberPaths = platformCollection.memberPaths.get().sorted()

    memberPaths.forEach { memberPath ->
        dependencies.add("implementation", project(memberPath))
    }

    tasks.named<ShadowJar>("shadowJar").configure {
        archiveClassifier.set("BUNDLE")

        memberPaths.forEach { memberPath ->
            val memberProject = rootProject.project(memberPath)
            memberProject.extensions.getByType<PlatformExtension>().descriptors.get().forEach { descriptor ->
                from({
                    val memberJar = memberProject
                        .tasks
                        .named<ShadowJar>("shadowJar")
                        .flatMap { it.archiveFile }
                        .get()
                        .asFile

                    zipTree(memberJar).matching {
                        include(descriptor)
                    }
                })
            }
        }

        manifest {
            attributes(
                "Plugin-Type" to "BUNDLE",
                "Supported-Platforms" to memberPaths.joinToString(",") { memberPath ->
                    rootProject.project(memberPath).extensions.getByType<PlatformExtension>().name.get()
                }
            )
        }
    }
}
