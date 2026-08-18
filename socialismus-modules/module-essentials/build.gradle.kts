plugins {
    base
}

tasks.register("essentialsModules") {
    group = "build"
    description = "Builds all Essentials module artifacts."

    dependsOn(
        ":module-essentials-api:build",
        ":module-essentials-common:build",
        ":module-essentials-command:build",
        ":module-essentials-dialogue:build",
        ":module-essentials-feature:build",
        ":module-essentials-runtime:build"
    )
}
