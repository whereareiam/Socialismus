plugins {
    id("maven-publish")
}

repositories {
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
}

dependencies {
    "compileOnly"(libs.bundles.adventure)
    "compileOnly"(libs.libby.core)
}

java {
    withSourcesJar()
    withJavadocJar()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            groupId = "me.whereareiam"
            artifactId = rootProject.name
            version = rootProject.version.toString()

            from(components["java"])
        }
    }
}

tasks.withType<Javadoc> {
    (options as StandardJavadocDocletOptions).apply {
        addStringOption("Xdoclint:none", "-quiet")
        title = "Socialismus API"
        windowTitle = "Socialismus API"
    }
}