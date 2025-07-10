repositories {
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
}

dependencies {
    "compileOnly"(project(":socialismus-common-api"))
    "implementation"(libs.libby.core)

    "compileOnly"(libs.bundles.adventure)
}