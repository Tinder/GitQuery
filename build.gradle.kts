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
    tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class.java).configureEach {
        kotlinOptions.jvmTarget = "1.8"
    }
}

tasks.register<Exec>("installGitHooks") {
    workingDir = project.projectDir

    commandLine("./build-support/bin/install-git-hooks")
}
