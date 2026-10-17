plugins {
    id("module-java")
}

dependencies {
    compileOnly(libs.commandant)
    compileOnly(libs.cloud.annotations)
    compileOnly(projects.moduleEssentialsApi)
    testImplementation(projects.moduleEssentialsApi)
}
