plugins {
    id("module-java")
}

dependencies {
    compileOnly(projects.moduleChirperApi)
    testImplementation(projects.moduleChirperApi)
}
