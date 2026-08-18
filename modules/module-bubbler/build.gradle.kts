plugins {
    base
}

tasks.register("bubblerModules") {
    group = "build"
    description = "Builds all Bubbler module artifacts."

    dependsOn(
        ":module-bubbler-api:build",
        ":module-bubbler-common:build",
        ":module-bubbler-command:build",
        ":module-bubbler-runtime:build"
    )
}
