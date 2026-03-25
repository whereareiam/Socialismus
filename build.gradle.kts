defaultTasks("shadowJar")

plugins {
    id("whereami.java.version") apply false
    id("whereami.module.info") apply false
    alias(libs.plugins.shadow) apply false
}
