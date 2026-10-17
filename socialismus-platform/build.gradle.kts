plugins {
    base
}

tasks.register("platformModules") {
    group = "build"
    description = "Builds all Socialismus platform modules."

    dependsOn(
        ":socialismus-platform:bundle:build",
        ":platform-bukkit:build",
        ":platform-proxy:build"
    )
}
