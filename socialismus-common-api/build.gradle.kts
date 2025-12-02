plugins {
    alias(libs.plugins.buildconfig)
    `java-library`
}

repositories {
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
}

dependencies {
    "compileOnly"(libs.ormlite)
    "api"(rootProject.libs.guice)
    "api"(rootProject.libs.annotations)
    "api"(rootProject.libs.configura)
    "api"(rootProject.libs.commandant)
    "api"(rootProject.libs.keystone)
    "api"(rootProject.libs.bundles.adventure)
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
            from(components["java"])
            artifactId = "Socialismus"
            pom {
                name.set("Socialismus")
                description.set("Public API for Socialismus - Minecraft communication management plugin")
            }
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
