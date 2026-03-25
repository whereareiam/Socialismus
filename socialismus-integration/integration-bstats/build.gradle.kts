plugins {
    id("whereami.convention.basic")
}

dependencies {
    compileOnly(libs.bundles.bStats)
    compileOnly(projects.socialismusApi)
}