plugins {
    id("packaging-module-jar")
}

socialismusModule {
    name.set("SocialismusLink")
}

dependencies {
    implementation(projects.moduleLinkCommon)

    compileOnly(projects.moduleNetworkApi)
}
