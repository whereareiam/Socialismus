import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import me.whereareiam.socialismus.buildlogic.packaging.DowngradeExtension
import me.whereareiam.socialismus.buildlogic.packaging.JarDowngrade

plugins {
    id("packaging-shaded-jar")
}

val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
val downgrade = extensions.create<DowngradeExtension>("downgrade")

// The stand-ins of newer Java APIs that a downgraded jar calls; JvmDowngrader copies the ones it needs into the jar.
val downgradeJavaApi = configurations.create("downgradeJavaApi") {
    isCanBeConsumed = false
    isTransitive = false
}

dependencies {
    add("downgradeJavaApi", libs.findLibrary("jvmdowngrader-java-api").get())
}

val compileClasspath = configurations.named("compileClasspath")
val shadePrefix = "me/whereareiam/socialismus/library/jvmdg/${project.name.replace('-', '_')}/"

// The shaded jar is the downgraded one, so every task and project that takes it gets the jar that runs on old Java.
tasks.named<ShadowJar>("shadowJar").configure {
    inputs.property("downgradeJava", downgrade.java)
    inputs.files(downgradeJavaApi).withPropertyName("downgradeJavaApi").withNormalizer(ClasspathNormalizer::class.java)
    doLast(JarDowngrade(
        downgrade.java,
        downgrade.untouched,
        downgradeJavaApi,
        compileClasspath.get(),
        shadePrefix
    ))
}
