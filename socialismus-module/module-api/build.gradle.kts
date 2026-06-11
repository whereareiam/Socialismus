plugins {
    id("api")
}

group = "me.whereareiam.socialismus.module"

dependencies {
    api(project(":socialismus-api"))
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
