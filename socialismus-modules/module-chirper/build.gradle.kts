plugins {
    base
}

tasks.register("chirperModules") {
    group = "build"
    description = "Builds all Chirper module artifacts."

    dependsOn(
        ":module-chirper-api:build",
        ":module-chirper-common:build",
        ":module-chirper-command:build",
        ":module-chirper-runtime:build"
    )
}
