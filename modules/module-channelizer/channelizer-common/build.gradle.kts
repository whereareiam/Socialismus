plugins {
    id("module-java")
}

dependencies {
    compileOnly(projects.moduleChannelizerApi)
    compileOnly(projects.moduleNetworkApi)

    testImplementation(projects.moduleChannelizerApi)
    testImplementation(projects.moduleNetworkApi)
}
