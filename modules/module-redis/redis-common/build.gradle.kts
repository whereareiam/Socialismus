plugins {
    id("module-java")
    alias(libs.plugins.attache)
}

dependencies {
    compileOnly(projects.moduleRedisApi)

    // Loaded into the module's class loader when SocialismusRedis loads.
    attache(libs.jedis)

    testImplementation(projects.moduleRedisApi)
}
