import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import me.whereareiam.socialismus.buildlogic.packaging.PlatformCollectionExtension
import me.whereareiam.socialismus.buildlogic.packaging.PlatformExtension

plugins {
    id("packaging-shaded-jar")
}

val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
val platform = extensions.create<PlatformExtension>("platform")
val platformCollection = PlatformCollectionExtension.of(project)

dependencies {
    add("implementation", project(":socialismus-api"))
    add("implementation", project(":socialismus-common"))
    add("implementation", project(":socialismus-platform:common"))
    add("implementation", project(":socialismus-module:module"))
    add("implementation", project(":socialismus-adapter-command"))
    add("implementation", project(":integration-bstats"))
    add("implementation", project(":integration-packetevents"))
    add("compileOnly", libs.findBundle("cloud").get())
}

tasks.withType<ShadowJar>().configureEach {
    archiveClassifier.set(platform.name)

    manifest {
        attributes["Plugin-Type"] = platform.name
    }
}

// Velocity reads its descriptor from an annotation; the Bukkit family reads these files.
tasks.named<ProcessResources>("processResources").configure {
    inputs.property("projectVersion", project.version)

    filesMatching(listOf("plugin.yml", "paper-plugin.yml")) {
        expand(
            mapOf(
                "projectName" to rootProject.name,
                "projectVersion" to project.version
            )
        )
    }
}

afterEvaluate {
    if (platform.bundled.get())
        platformCollection.memberPaths.add(project.path)
}
