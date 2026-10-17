plugins {
    id("module-java")
}

dependencies {
    api(projects.socialismusModule.moduleApi)

    implementation(projects.socialismusModule.moduleCommon)
}
