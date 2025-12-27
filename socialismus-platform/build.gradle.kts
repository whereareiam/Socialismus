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

        relocate("me.whereareiam.attache", "me.whereareiam.socialismus.library.attache")
        relocate("org.bstats", "me.whereareiam.socialismus.library.bStats")

        relocate("com.fasterxml.jackson", "me.whereareiam.socialismus.library.jackson")

        relocate("com.google.common", "me.whereareiam.socialismus.library.guava")
        relocate("com.google.inject", "me.whereareiam.socialismus.library.guice")

        // Exclude API dependencies (including transitives) - these are provided at runtime, not shaded
        val shadowJar = this
        val apiProject = project(":socialismus-api")
        afterEvaluate {
            apiProject.configurations.findByName("shadowExcludes")
                ?.resolvedConfiguration
                ?.resolvedArtifacts
                ?.forEach { artifact ->
                    val id = artifact.moduleVersion.id
                    shadowJar.dependencies {
                        exclude(dependency("${id.group}:${id.name}:.*"))
                    }
                }
        }

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
