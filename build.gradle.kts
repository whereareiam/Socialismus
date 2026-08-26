import me.whereareiam.attache.plugin.gradle.extension.AttacheExtension
import org.gradle.api.tasks.Sync

val buildVersion = System.getenv("VERSION") ?: "dev"
val socialismusModuleDependencyPattern =
    "((?:[0-9]+\\\\.){2}[0-9]+(?:-[A-Za-z0-9._-]+)?|dev(?:-[A-Za-z0-9._-]+)?)"

extra["socialismusModuleDependencyPattern"] = socialismusModuleDependencyPattern

plugins {
    alias(libs.plugins.attache)
    alias(libs.plugins.spawner)
    id("dev-scenarios")
}

allprojects {
    group = "me.whereareiam"
    version = buildVersion
}

extensions.configure<AttacheExtension>("attache") {
    transitive.set(true)

    mavenLocal()
    repository("https://registry.whereareiam.me/maven/packages")
}

val moduleArtifactNames = listOf(
    "Chirper",
    "Bubbler",
    "Essentials",
    "SocialismusRedis",
    "SocialismusChannelizer"
)

defaultTasks("jars")

tasks.register("pluginJars") {
    group = "build"
    description = "Builds the shaded Socialismus jars for all supported runtimes."

    dependsOn(
        ":socialismus-api:jar",
        ":platform-velocity:shadowJar",
        ":platform-bukkit-bukkit:shadowJar",
        ":platform-bukkit-paper:shadowJar"
    )
}

tasks.register("moduleJars") {
    group = "build"
    description = "Builds all migrated Socialismus module jars."

    dependsOn(":modules:moduleArtifacts")
}

tasks.register<Sync>("jars") {
    group = "build"
    description = "Builds and collects Socialismus and module jars."

    dependsOn("pluginJars", "moduleJars")

    from(layout.buildDirectory.dir("libs")) {
        include("${rootProject.name}-$buildVersion-PAPER.jar")
        include("${rootProject.name}-$buildVersion-BUKKIT.jar")
        include("${rootProject.name}-$buildVersion-VELOCITY.jar")
        moduleArtifactNames.forEach { include("$it-$buildVersion.jar") }

        eachFile {
            name = when (name) {
                "${rootProject.name}-$buildVersion-PAPER.jar" -> "${rootProject.name}-PAPER-$buildVersion.jar"
                "${rootProject.name}-$buildVersion-BUKKIT.jar" -> "${rootProject.name}-BUKKIT-$buildVersion.jar"
                "${rootProject.name}-$buildVersion-VELOCITY.jar" -> "${rootProject.name}-VELOCITY-$buildVersion.jar"
                else -> name
            }
        }
    }

    from(project(":socialismus-api").layout.buildDirectory.dir("libs")) {
        include("socialismus-api-$buildVersion.jar")
        rename("socialismus-api-$buildVersion.jar", "${rootProject.name}-API-$buildVersion.jar")
    }

    into(layout.buildDirectory.dir("jars"))
}
