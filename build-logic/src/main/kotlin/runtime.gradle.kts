import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.jvm.tasks.Jar

plugins {
    id("shared")
    id("relocations")
}

tasks.withType<ShadowJar>().configureEach {
    archiveBaseName.set(rootProject.name)

    val defaultDestination = rootProject.layout.buildDirectory.dir("libs")

    if (providers.gradleProperty("output").isPresent) {
        destinationDirectory.set(file(providers.gradleProperty("output").get()))
    } else if (project.path != ":platform-bukkit-common") {
        destinationDirectory.set(defaultDestination)
    }
}

tasks.named<Jar>("jar").configure {
    dependsOn(tasks.named("shadowJar"))
}
