plugins {
    `java-library`
    id("io.freefair.lombok")
}

val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

group = "me.whereareiam"

// Compilation and tests use Java 21 whichever Java runs Gradle.
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

lombok {
    version.set(libs.findVersion("lombok").get().requiredVersion)
}

dependencies {
    add("compileOnly", libs.findLibrary("guice").get())
    add("compileOnly", libs.findLibrary("annotations").get())
    add("compileOnly", libs.findLibrary("configura").get())
    add("compileOnly", libs.findLibrary("configura-feature-extension").get())
    add("compileOnly", libs.findLibrary("configura-feature-postprocess").get())
    add("compileOnly", libs.findLibrary("configura-feature-polymorphic").get())
    add("compileOnly", libs.findLibrary("commandant").get())
    add("compileOnly", libs.findLibrary("keystone").get())
    add("compileOnly", libs.findLibrary("strata").get())
    add("compileOnly", libs.findLibrary("strata-adapter-configura").get())
    add("compileOnly", libs.findBundle("adventure").get())
    add("implementation", libs.findLibrary("attache-common").get())

    add("testRuntimeOnly", libs.findLibrary("junit-platform").get())
    add("testImplementation", libs.findBundle("testing").get())
    add("testImplementation", libs.findLibrary("guice").get())
    add("testImplementation", libs.findLibrary("configura").get())
    add("testImplementation", libs.findLibrary("configura-feature-extension").get())
    add("testImplementation", libs.findLibrary("configura-feature-postprocess").get())
    add("testImplementation", libs.findLibrary("configura-feature-polymorphic").get())
    add("testImplementation", libs.findLibrary("commandant").get())
    add("testImplementation", libs.findLibrary("keystone").get())
    add("testImplementation", libs.findLibrary("strata").get())
    add("testImplementation", libs.findLibrary("strata-adapter-configura").get())
    add("testImplementation", libs.findBundle("adventure").get())

    if (path != ":socialismus-api") {
        add("compileOnly", project(":socialismus-api"))
        add("testImplementation", project(":socialismus-api"))
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
    ignoreFailures = providers.gradleProperty("ignoreTestFailures")
        .map(String::toBoolean)
        .orElse(false)
        .get()
}

// Tests tagged "integration" start servers in containers, so they run in integrationTest instead of test.
tasks.named<Test>("test") {
    useJUnitPlatform {
        excludeTags("integration")
    }
}

val integrationTest = tasks.register<Test>("integrationTest") {
    description = "Runs the tests that start servers, such as databases, in containers."
    group = LifecycleBasePlugin.VERIFICATION_GROUP
    testClassesDirs = sourceSets["test"].output.classesDirs
    classpath = sourceSets["test"].runtimeClasspath
    useJUnitPlatform {
        includeTags("integration")
    }
    // Most modules have no such test.
    failOnNoDiscoveredTests = false
    shouldRunAfter(tasks.named("test"))
}

tasks.named("check") {
    dependsOn(integrationTest)
}
