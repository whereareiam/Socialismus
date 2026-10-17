plugins {
    id("module-java")
}

dependencies {
    compileOnly(projects.moduleEssentialsApi)
    testImplementation(projects.moduleEssentialsApi)
}
