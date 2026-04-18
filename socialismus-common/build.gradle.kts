repositories {
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
}

dependencies {
    "compileOnly"(project(":socialismus-api"))
    "compileOnly"(libs.bundles.adventure)
    
    // test dependencies
    "testImplementation"(project(":socialismus-api"))
    "testImplementation"(libs.bundles.adventure)
}
