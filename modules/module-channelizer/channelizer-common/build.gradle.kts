plugins {
    id("module-java")
}

dependencies {
    compileOnly(projects.moduleChannelizerApi)
    testImplementation(projects.moduleChannelizerApi)
}
