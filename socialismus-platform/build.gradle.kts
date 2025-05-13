import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    alias(libs.plugins.shadow)
}

subprojects {
    plugins.apply(rootProject.libs.plugins.shadow.get().pluginId)

    if (name == "platform-bukkit") {
        tasks.named<Jar>("jar").configure { enabled = false }
        tasks.named("shadowJar").configure { enabled = false }
    }

    tasks.withType<ShadowJar> {
        archiveBaseName.set(rootProject.name)

        relocate("com.alessiodp.libby", "me.whereareiam.socialismus.library.libby")
        relocate("org.bstats", "me.whereareiam.socialismus.library.bStats")

        relocate("com.fasterxml.jackson", "me.whereareiam.socialismus.library.jackson")

        relocate("com.google.common", "me.whereareiam.socialismus.library.guava")
        relocate("com.google.inject", "me.whereareiam.socialismus.library.guice")

        val defaultDestination = rootProject.layout.buildDirectory.dir("libs")

        val customOutputDir = if (project.hasProperty("output")) {
            project.layout.dir(project.provider { File(project.property("output").toString()) })
        } else {
            null
        }

        if (project.name != "common")
            destinationDirectory.set(customOutputDir ?: defaultDestination)
    }

    repositories {
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
        maven("https://repo.papermc.io/repository/maven-public/")
    }

    dependencies {
        "implementation"(project(":socialismus-integration:integration-packetevents"))
        "implementation"(project(":socialismus-integration:integration-bstats"))
        "compileOnly"(rootProject.libs.bundles.cloud)

        rootProject.allprojects
            .filter { it != project && it.parent == rootProject }
            .forEach { subproject ->
                if (subproject.name != "socialismus-platform" && subproject.name != "socialismus-integration")
                    "implementation"(project(":${subproject.name}"))
            }
    }

    tasks.named<Jar>("jar") {
        dependsOn("shadowJar")
    }
}
