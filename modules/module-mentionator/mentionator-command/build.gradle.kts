plugins {
    id("module-java")
}

dependencies {
    compileOnly(libs.commandant)
    compileOnly(libs.cloud.annotations)
    compileOnly(projects.moduleMentionatorApi)
    testImplementation(projects.moduleMentionatorApi)
}
