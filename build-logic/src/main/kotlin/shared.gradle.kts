plugins {
    `java-library`
}

val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
val buildVersion = providers.environmentVariable("VERSION").orElse("dev")

group = "me.whereareiam"
version = buildVersion.get()

tasks.withType<JavaCompile>().configureEach {
    sourceCompatibility = JavaVersion.VERSION_17.toString()
    targetCompatibility = JavaVersion.VERSION_17.toString()
}

dependencies {
    add("compileOnly", libs.findLibrary("lombok").get())
    add("annotationProcessor", libs.findLibrary("lombok").get())
    add("testImplementation", libs.findLibrary("lombok").get())
    add("testAnnotationProcessor", libs.findLibrary("lombok").get())

    add("compileOnly", libs.findLibrary("guice").get())
    add("compileOnly", libs.findLibrary("annotations").get())
    add("compileOnly", libs.findLibrary("configura").get())
    add("compileOnly", libs.findLibrary("configura-feature-extension").get())
    add("compileOnly", libs.findLibrary("configura-feature-postprocess").get())
    add("compileOnly", libs.findLibrary("configura-feature-polymorphic").get())
    add("compileOnly", libs.findLibrary("commandant").get())
    add("compileOnly", libs.findLibrary("keystone").get())
    add("compileOnly", libs.findBundle("adventure").get())
    add("implementation", libs.findLibrary("attache-common").get())

    add("testImplementation", libs.findLibrary("configura").get())
    add("testImplementation", libs.findLibrary("configura-feature-extension").get())
    add("testImplementation", libs.findLibrary("configura-feature-postprocess").get())
    add("testImplementation", libs.findLibrary("configura-feature-polymorphic").get())
    add("testImplementation", libs.findLibrary("commandant").get())
    add("testImplementation", libs.findLibrary("keystone").get())
    add("testImplementation", libs.findLibrary("guice").get())
    add("testImplementation", libs.findBundle("testing").get())
    add("testRuntimeOnly", libs.findLibrary("junit-platform").get())

    if (path != ":socialismus-api") {
        add("compileOnly", project(":socialismus-api"))
        add("testImplementation", project(":socialismus-api"))
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}
