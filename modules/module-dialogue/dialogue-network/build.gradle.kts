plugins {
    id("module-java")
}

dependencies {
    compileOnly(projects.moduleDialogueApi)
    compileOnly(projects.moduleDialogueCommon)
    compileOnly(projects.moduleNetworkApi)

    testImplementation(projects.moduleDialogueApi)
    testImplementation(projects.moduleDialogueCommon)
    testImplementation(projects.moduleNetworkApi)
    testImplementation(projects.moduleNetworkCommon)
    testImplementation(testFixtures(projects.moduleNetworkCommon))
}
