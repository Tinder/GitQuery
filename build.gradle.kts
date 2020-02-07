plugins {
    base
    kotlin("jvm") version "1.3.61" apply false
    id("com.vanniktech.maven.publish") version "0.8.0" apply false
}

allprojects {
    group = project.group
    version = project.version
    repositories {
        jcenter()
        mavenCentral()
    }
}
