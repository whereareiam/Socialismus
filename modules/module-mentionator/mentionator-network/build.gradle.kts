plugins {
    id("module-java")
}

dependencies {
    compileOnly(projects.moduleMentionatorApi)
    compileOnly(projects.moduleMentionatorCommon)
    compileOnly(projects.moduleNetworkApi)

    testImplementation(projects.moduleMentionatorApi)
    testImplementation(projects.moduleMentionatorCommon)
    testImplementation(projects.moduleNetworkApi)
    testImplementation(projects.moduleNetworkCommon)
    testImplementation(testFixtures(projects.moduleNetworkCommon))
}
