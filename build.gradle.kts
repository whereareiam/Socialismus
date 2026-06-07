import me.whereareiam.attache.plugin.gradle.extension.AttacheExtension

plugins {
    alias(libs.plugins.attache)
    alias(libs.plugins.spawner)
    id("dev-scenarios")
}

extensions.configure<AttacheExtension>("attache") {
    transitive.set(true)

    mavenLocal()
    repository("https://maven.whereareiam.me/release")
    repository("https://maven.whereareiam.me/development")
}

defaultTasks("pluginJars")

tasks.register("pluginJars") {
    group = "build"
    description = "Builds shaded plugin jars for all supported runtime platforms."

    dependsOn(
        ":platform-velocity:shadowJar",
        ":platform-bukkit-bukkit:shadowJar",
        ":platform-bukkit-paper:shadowJar"
    )
}
