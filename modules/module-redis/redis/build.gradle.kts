plugins {
    id("packaging-module-jar")
}

socialismusModule {
    name.set("SocialismusRedis")
}

dependencies {
    implementation(projects.moduleRedisApi)
    implementation(projects.moduleRedisCommon)
}
