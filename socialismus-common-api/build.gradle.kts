plugins {
    id("maven-publish")
    alias(libs.plugins.delombok)
}

repositories {
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
}

dependencies {
    compileOnly(libs.bundles.adventure)
    compileOnly(libs.libby.core)
}

tasks.withType<Javadoc> {
    (options as StandardJavadocDocletOptions).apply {
        addStringOption("Xdoclint:none", "-quiet")
        title = "Socialismus API"
        windowTitle = "Socialismus API"
    }
}

val delombokTask = tasks.named("delombok")

val javadocJar by tasks.registering(Jar::class) {
    dependsOn(tasks.javadoc)
    from(tasks.javadoc)
    archiveClassifier.set("javadoc")
}

val sourcesJar by tasks.registering(Jar::class) {
    dependsOn(delombokTask)
    archiveClassifier.set("sources")
    from(delombokTask)
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            groupId = "me.whereareiam"
            artifactId = rootProject.name
            version = rootProject.version.toString()

            from(components["java"])
            artifact(sourcesJar.get())
            artifact(javadocJar.get())
        }
    }
}
