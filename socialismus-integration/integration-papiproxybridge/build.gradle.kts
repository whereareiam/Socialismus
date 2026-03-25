plugins {
    id("whereami.convention.basic")
}

dependencies {
    compileOnly(libs.papiProxyBridge)
    compileOnly(projects.socialismusApi)
}