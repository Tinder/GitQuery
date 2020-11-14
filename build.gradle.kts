plugins {
    base
    kotlin("jvm") version Libs.Versions.kotlin apply false
    id("com.vanniktech.maven.publish") version Libs.Versions.vanniktechMavenPublish apply false
    id("com.gradle.plugin-publish") version Libs.Versions.gradlePluginPublish apply false
    id("org.jetbrains.dokka") version Libs.Versions.dokkaGradlePlugin
    id("org.jlleitschuh.gradle.ktlint") version Libs.Versions.ktlintGradlePlugin
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

subprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
    ktlint {
        version.set(Libs.Versions.ktlint)
        filter {
            exclude("**/generated/**")
            include("**/kotlin/**")
        }
    }
}

tasks.register<Exec>("installGitHooks") {
    workingDir = project.rootDir

    commandLine("./build-support/bin/install-git-hooks")
}
