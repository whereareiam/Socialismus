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
