plugins {
    base
    kotlin("jvm") version Libs.Versions.kotlin apply false
    id("com.vanniktech.maven.publish") version Libs.Versions.vanniktechMavenPublish apply false
    id("com.gradle.plugin-publish") version Libs.Versions.gradlePluginPublish apply false
    id("org.jetbrains.dokka") version Libs.Versions.dokkaGradlePlugin
}

allprojects {
    group = project.group
    version = project.version
    repositories {
        jcenter()
        mavenCentral()
    }
}
