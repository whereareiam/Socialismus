plugins {
    `java-test-fixtures`
    id("module-java")
}

dependencies {
    compileOnly(projects.moduleNetworkApi)

    // The in-memory network and the contracts every transport and store must meet, for the transport modules' tests.
    testFixturesApi(projects.moduleNetworkApi)
    testFixturesApi(projects.socialismusApi)
    testFixturesApi(libs.annotations)
    testFixturesApi(libs.bundles.testing)
    testFixturesImplementation(libs.configura)

    testImplementation(projects.moduleNetworkApi)
    // Socialismus's own serialization, which carries chat components as it does in production.
    testImplementation(projects.socialismusCommon)
}
