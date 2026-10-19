plugins {
    id("packaging-module-jar")
}

socialismusModule {
    name.set("Dialogue")
}

dependencies {
    implementation(projects.moduleDialogueApi)
    implementation(projects.moduleDialogueCommand)
    implementation(projects.moduleDialogueCommon)
    implementation(projects.moduleDialogueNetwork)
}
