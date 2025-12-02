plugins {
    id("maven-publish")
    alias(libs.plugins.buildconfig)
}

repositories {
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
}

dependencies {
    "compileOnly"(libs.bundles.adventure)
    "compileOnly"(libs.attache.common)
    "compileOnly"(libs.ormlite)
}

buildConfig {
    packageName("me.whereareiam.socialismus")

    // Add basic project info
    buildConfigField("String", "NAME", "\"${rootProject.name}\"")
    buildConfigField("String", "VERSION", "\"${rootProject.version}\"")

    // Automatically expose all versions from the version catalog
    val catalog = rootProject.extensions.getByType<VersionCatalogsExtension>().named("libs")
    catalog.versionAliases.forEach { alias ->
        val version = catalog.findVersion(alias).get().toString()
        // Convert alias to valid Java constant name (e.g., "adventure-platform-bukkit" -> "ADVENTURE_PLATFORM_BUKKIT")
        // Replace both dashes and dots with underscores
        val fieldName = alias.replace("-", "_").replace(".", "_").uppercase()
        buildConfigField("String", fieldName, "\"$version\"")
    }
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
