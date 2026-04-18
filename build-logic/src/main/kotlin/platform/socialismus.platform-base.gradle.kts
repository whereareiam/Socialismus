import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.jvm.tasks.Jar

plugins {
    id("socialismus.java-common")
    id("com.gradleup.shadow")
}

tasks.withType<ShadowJar>().configureEach {
    archiveBaseName.set(rootProject.name)

    relocate("me.whereareiam.attache", "me.whereareiam.socialismus.library.attache")
    relocate("org.bstats", "me.whereareiam.socialismus.library.bStats")
    relocate("com.fasterxml.jackson", "me.whereareiam.socialismus.library.jackson")
    relocate("com.google.common", "me.whereareiam.socialismus.library.guava")
    relocate("com.google.inject", "me.whereareiam.socialismus.library.guice")

    dependencies {
        exclude(dependency("com.google.inject:guice:.*"))
        exclude(dependency("org.jetbrains:annotations:.*"))
        exclude(dependency("me.whereareiam:configura:.*"))
        exclude(dependency("me.whereareiam:commandant:.*"))
        exclude(dependency("me.whereareiam:keystone:.*"))
        exclude(dependency("net.kyori:adventure-api:.*"))
        exclude(dependency("net.kyori:adventure-text-minimessage:.*"))
        exclude(dependency("net.kyori:adventure-text-serializer-legacy:.*"))
        exclude(dependency("net.kyori:adventure-text-serializer-plain:.*"))
        exclude(dependency("net.kyori:adventure-text-serializer-gson:.*"))
    }

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
