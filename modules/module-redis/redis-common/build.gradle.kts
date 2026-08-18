plugins {
    id("shared")
}

dependencies {
    compileOnly(libs.jedis)
    compileOnly(project(":module-redis-api"))
    testImplementation(project(":module-redis-api"))
}
