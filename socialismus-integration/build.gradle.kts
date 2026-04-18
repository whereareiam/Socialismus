plugins {
    base
}

tasks.register("integrationModules") {
    group = "build"
    description = "Builds all Socialismus integration modules."

    dependsOn(
        ":integration-bstats:build",
        ":integration-packetevents:build",
        ":integration-papiproxybridge:build",
        ":integration-placeholderapi:build"
    )
}
