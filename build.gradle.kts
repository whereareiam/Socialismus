import me.whereareiam.attache.plugin.gradle.extension.AttacheExtension
import me.whereareiam.toolkit.versioning.extension.ToolkitVersioningExtension

plugins {
    alias(libs.plugins.attache)
    alias(libs.plugins.spawner)
    alias(libs.plugins.toolkit.distribution)
    alias(libs.plugins.toolkit.versioning)
    id("dev-scenarios")
}

allprojects {
    version = rootProject.extensions
        .getByType(ToolkitVersioningExtension::class.java)
        .resolvedVersion()
        .get()
}

extensions.configure<AttacheExtension>("attache") {
    transitive.set(true)

    mavenLocal()
    repository("https://registry.whereareiam.me/maven/packages")
}

defaultTasks("pluginJars")

tasks.register("pluginJars") {
    group = "build"
    description = "Builds the shaded Socialismus jars for all supported platforms."

    dependsOn(
        ":socialismus-api:jar",
        ":socialismus-platform:bundle:shadowJar",
        ":platform-proxy-velocity:shadowJar",
        ":platform-bukkit-bukkit:shadowJar",
        ":platform-bukkit-paper:shadowJar"
    )
}

// Files a development build or a release ships; assembleDistribution writes them to build/distribution.
toolkitDistribution {
    file("API", ":socialismus-api", "jar")
    // The bundle runs on Paper and Velocity; Modrinth takes each platform's jar on its own.
    file("BUNDLE", ":socialismus-platform:bundle", "shadowJar")
    file("PAPER", ":platform-bukkit-paper", "shadowJar")
    file("BUKKIT", ":platform-bukkit-bukkit", "shadowJar")
    file("VELOCITY", ":platform-proxy-velocity", "shadowJar")
    file("Chirper", ":module-chirper", "shadowJar")
    file("Bubbler", ":module-bubbler", "shadowJar")
    file("Dialogue", ":module-dialogue", "shadowJar")
    // Essentials builds, but carries no feature since private messages became Dialogue, so it is not shipped.
    file("Redis", ":module-redis", "shadowJar")
    file("Channelizer", ":module-channelizer", "shadowJar")
    file("Database", ":module-database", "shadowJar")
    file("Network", ":module-network", "shadowJar")
}
