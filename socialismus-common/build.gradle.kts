plugins {
    id("whereami.convention.basic")
}

dependencies {
    compileOnly(libs.bundles.adventure)
    compileOnly(projects.socialismusApi)
    
    // test dependencies
    testImplementation(libs.bundles.adventure)
    testImplementation(projects.socialismusApi)
}

tasks.test {
    useJUnitPlatform()
}