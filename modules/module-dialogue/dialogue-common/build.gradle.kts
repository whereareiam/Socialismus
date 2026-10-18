plugins {
    id("module-java")
}

dependencies {
    compileOnly(projects.moduleDialogueApi)
    testImplementation(projects.moduleDialogueApi)
}
