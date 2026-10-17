plugins {
    id("module-java")
}

dependencies {
    compileOnly(projects.socialismusModule.moduleApi)
    compileOnly(libs.bundles.bStats)
}
