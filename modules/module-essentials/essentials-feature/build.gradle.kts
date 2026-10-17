plugins {
    id("module-java")
}

dependencies {
    implementation(projects.moduleEssentialsDialogue)
    compileOnly(projects.moduleEssentialsApi)
    testImplementation(projects.moduleEssentialsApi)
}
