plugins {
    id("module-java")
}

dependencies {
    compileOnly(projects.moduleNetworkApi)

    testImplementation(projects.moduleNetworkApi)
    testImplementation(testFixtures(projects.moduleNetworkCommon))
}
