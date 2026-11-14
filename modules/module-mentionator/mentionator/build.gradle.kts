plugins {
    id("packaging-module-jar")
}

socialismusModule {
    name.set("Mentionator")
}

dependencies {
    implementation(projects.moduleMentionatorApi)
    implementation(projects.moduleMentionatorCommand)
    implementation(projects.moduleMentionatorCommon)
    implementation(projects.moduleMentionatorNetwork)
}
