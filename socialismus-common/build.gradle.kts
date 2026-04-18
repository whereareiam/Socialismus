plugins {
    id("socialismus.java-common")
}

dependencies {
    compileOnly(projects.socialismusApi)
    testImplementation(projects.socialismusApi)
}
