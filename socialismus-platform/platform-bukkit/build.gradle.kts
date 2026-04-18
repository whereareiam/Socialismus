plugins {
    base
}

tasks.register("bukkitPlatforms") {
    group = "build"
    description = "Builds all Bukkit-family platform modules."

    dependsOn(
        ":platform-bukkit-common:build",
        ":platform-bukkit-bukkit:build",
        ":platform-bukkit-paper:build"
    )
}
