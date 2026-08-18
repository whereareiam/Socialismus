plugins {
    base
}

tasks.register("moduleArtifacts") {
    group = "build"
    description = "Builds all Socialismus module artifacts."

    dependsOn(
        ":socialismus-module:module-api:build",
        ":socialismus-module:module:build",
        ":module-chirper:chirperModules"
    )
}
