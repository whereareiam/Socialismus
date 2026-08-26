plugins {
    id("api")
}

group = "me.whereareiam.socialismus.module"

dependencies {
    compileOnly(libs.packetevents)
}

toolkitPublish {
    artifactId.set("Bubbler")

    pom {
        name.set("Bubbler")
        description.set("Public API for Bubbler - Socialismus chat bubble module")
    }

    javadoc {
        title.set("Bubbler API")
        windowTitle.set("Bubbler API")
    }
}
