plugins {
    id("module-java")
}

dependencies {
    compileOnly(projects.moduleMentionatorApi)

    testImplementation(projects.moduleMentionatorApi)
    // Socialismus's own stores and Configura setup, as the module meets them on a server.
    testImplementation(projects.socialismusCommon)
    testImplementation(libs.conditia.adapter.configura)
}
