plugins {
    id("packaging-module-jar")
}

socialismusModule {
    name.set("Chirper")
}

dependencies {
    implementation(projects.moduleChirperApi)
    implementation(projects.moduleChirperCommand)
    implementation(projects.moduleChirperCommon)
}
