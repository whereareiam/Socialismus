plugins {
    `java-library`
    id("socialismus.java-common")
    alias(libs.plugins.buildconfig)
}

dependencies {
    compileOnly(libs.ormlite)
    compileOnlyApi(libs.guice)
    compileOnlyApi(libs.annotations)
    compileOnlyApi(libs.configura)
    compileOnlyApi(libs.commandant)
    compileOnlyApi(libs.keystone)
    compileOnlyApi(libs.bundles.adventure)
}

buildConfig {
    packageName("me.whereareiam.socialismus")

    buildConfigField("String", "NAME", "\"${rootProject.name}\"")
    buildConfigField("String", "VERSION", "\"${rootProject.version}\"")

    val catalog = rootProject.extensions.getByType<VersionCatalogsExtension>().named("libs")
    catalog.versionAliases.forEach { alias ->
        val version = catalog.findVersion(alias).get().toString()
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

tasks.withType<Javadoc>().configureEach {
    (options as StandardJavadocDocletOptions).apply {
        addStringOption("Xdoclint:none", "-quiet")
        title = "Socialismus API"
        windowTitle = "Socialismus API"
    }
}

tasks.named<JavaCompile>("compileTestJava") {
    enabled = false
}

tasks.named<Test>("test") {
    enabled = false
}
