plugins {
    id("module-java")
}

dependencies {
    compileOnly(rootProject.libs.commandant)
    compileOnly(rootProject.libs.cloud.annotations)
    compileOnly(projects.moduleEssentialsApi)
    testImplementation(projects.moduleEssentialsApi)
}
