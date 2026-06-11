plugins {
    id("api")
    alias(libs.plugins.buildconfig)
}

group = "me.whereareiam.socialismus"

dependencies {
    compileOnly(libs.ormlite)
    compileOnlyApi(libs.guice)
    compileOnlyApi(libs.annotations)
    compileOnlyApi(libs.configura)
    compileOnlyApi(libs.configura.feature.extension.api)
    compileOnlyApi(libs.configura.feature.postprocess.api)
    compileOnlyApi(libs.configura.feature.polymorphic.api)
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

toolkitPublish {
    artifactId.set("api")

    pom {
        description.set("Public API for Socialismus - Minecraft communication management plugin")
        name.set("Socialismus API")
    }

    javadoc {
        title.set("Socialismus API")
        windowTitle.set("Socialismus API")
    }
}
