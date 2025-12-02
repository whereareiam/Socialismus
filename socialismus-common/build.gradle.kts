repositories {
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
}

dependencies {
    "compileOnly"(project(":socialismus-common-api"))
    "compileOnly"(libs.bundles.adventure)
    
    // test dependencies
    "testImplementation"(project(":socialismus-common-api"))
    "testImplementation"(libs.bundles.adventure)
}

tasks.test {
    useJUnitPlatform()
}