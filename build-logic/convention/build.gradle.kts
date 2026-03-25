plugins {
    `kotlin-dsl`
    id("java-gradle-plugin")
}

dependencies {
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
    implementation(libs.shadow)
}

gradlePlugin {
    plugins {
        create("whereami.java.version") {
            id = name
            implementationClass = "me.whereareiam.socialismus.gradle.plugin.JavaVersionPlugin"
        }

        create("whereami.module.info") {
            id = name
            implementationClass = "me.whereareiam.socialismus.gradle.plugin.ModuleInfoPlugin"
        }
        create("whereami.publish") {
            id = name
            implementationClass = "me.whereareiam.socialismus.gradle.plugin.PublishPlugin"
        }
    }
}
