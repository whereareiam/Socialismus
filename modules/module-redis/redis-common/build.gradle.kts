plugins {
    id("module-java")
    alias(libs.plugins.attache)
}

dependencies {
    compileOnly(projects.moduleNetworkApi)
    compileOnly(projects.moduleRedisApi)

    // Loaded into the module's class loader when SocialismusRedis loads.
    attache(libs.jedis)

    testImplementation(projects.moduleNetworkApi)
    testImplementation(projects.moduleNetworkCommon)
    testImplementation(projects.moduleRedisApi)
    testImplementation(testFixtures(projects.moduleNetworkCommon))
    testImplementation(libs.jedis)
    testImplementation(libs.testcontainers.junit)
}
