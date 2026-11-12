plugins {
    id("module-api")
}

group = "me.whereareiam.socialismus.module"

dependencies {
    api(projects.socialismusApi)
    api(libs.chassis.api)
    api(libs.chassis.adapter.attache)
}

toolkitPublish {
    artifactId.set("api")

    pom {
        description.set("Module API for Socialismus modules")
        name.set("Socialismus Module API")
    }

    javadoc {
        title.set("Socialismus Module API")
        windowTitle.set("Socialismus Module API")
    }
}
