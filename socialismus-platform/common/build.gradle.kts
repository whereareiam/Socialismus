plugins {
    id("module-java")
}

dependencies {
    compileOnly(projects.integrationBstats)
    compileOnly(projects.integrationPacketevents)
    compileOnly(projects.socialismusAdapterCommand)
    compileOnly(projects.socialismusCommon)
    compileOnly(projects.socialismusModule.module)

    compileOnly(libs.bStats)
}
