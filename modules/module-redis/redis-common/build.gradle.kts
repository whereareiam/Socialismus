plugins {
    id("module-java")
}

dependencies {
    compileOnly(libs.jedis)
    compileOnly(projects.moduleRedisApi)
    testImplementation(projects.moduleRedisApi)
}
