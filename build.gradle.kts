plugins {
    base
    kotlin("jvm") version "1.3.61" apply false
    id("com.vanniktech.maven.publish") version "0.10.0" apply false
    id("com.gradle.plugin-publish") version "0.11.0" apply false
}

allprojects {
    group = project.group
    version = project.version
    repositories {
        jcenter()
        mavenCentral()
    }
}
