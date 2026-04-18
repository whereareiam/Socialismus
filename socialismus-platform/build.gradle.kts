plugins {
    base
}

tasks.register("platformModules") {
    group = "build"
    description = "Builds all Socialismus platform modules."

    dependsOn(
        ":platform-velocity:build",
        ":platform-bukkit:build"
    )
}
