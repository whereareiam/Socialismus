plugins {
    id("whereami.convention.basic")
}

dependencies {
    compileOnly(libs.placeholderAPI)
    compileOnly(libs.spigot)
    compileOnly(projects.socialismusApi)
}