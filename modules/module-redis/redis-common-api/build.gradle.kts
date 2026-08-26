plugins {
    id("api")
}

group = "me.whereareiam.socialismus.module"

toolkitPublish {
    artifactId.set("SocialismusRedis")

    pom {
        name.set("SocialismusRedis")
        description.set("Public API for SocialismusRedis - Socialismus Redis resource provider module")
    }

    javadoc {
        title.set("SocialismusRedis API")
        windowTitle.set("SocialismusRedis API")
    }
}
