import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("com.gradleup.shadow")
}

tasks.withType<ShadowJar>().configureEach {
    relocate("me.whereareiam.attache", "me.whereareiam.socialismus.library.attache")
    relocate("org.bstats", "me.whereareiam.socialismus.library.bStats")
    relocate("com.fasterxml.jackson", "me.whereareiam.socialismus.library.jackson")
    relocate("com.google.common", "me.whereareiam.socialismus.library.guava")
    relocate("com.google.inject", "me.whereareiam.socialismus.library.guice")

    dependencies {
        exclude(dependency("com.google.inject:guice:.*"))
        exclude(dependency("org.jetbrains:annotations:.*"))
        exclude(dependency("me.whereareiam:configura:.*"))
        exclude(dependency("me.whereareiam.configura.feature:extension:.*"))
        exclude(dependency("me.whereareiam.configura.feature:extension-api:.*"))
        exclude(dependency("me.whereareiam.configura.feature:postprocess:.*"))
        exclude(dependency("me.whereareiam.configura.feature:postprocess-api:.*"))
        exclude(dependency("me.whereareiam.configura.feature:polymorphic:.*"))
        exclude(dependency("me.whereareiam.configura.feature:polymorphic-api:.*"))
        exclude(dependency("me.whereareiam:commandant:.*"))
        exclude(dependency("me.whereareiam:commandant-common:.*"))
        exclude(dependency("me.whereareiam:keystone:.*"))
        exclude(dependency("net.kyori:adventure-api:.*"))
        exclude(dependency("net.kyori:adventure-text-minimessage:.*"))
        exclude(dependency("net.kyori:adventure-text-serializer-legacy:.*"))
        exclude(dependency("net.kyori:adventure-text-serializer-plain:.*"))
        exclude(dependency("net.kyori:adventure-text-serializer-gson:.*"))
    }
}
