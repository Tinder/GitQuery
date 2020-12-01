import io.gitlab.arturbosch.detekt.Detekt
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    base
    kotlin("jvm") version Libs.Versions.kotlin apply false
    id("com.gradle.plugin-publish") version Libs.Versions.gradlePluginPublish apply false
    id("com.vanniktech.maven.publish") version Libs.Versions.vanniktechMavenPublish apply false
    id("io.gitlab.arturbosch.detekt") version(Libs.Versions.detektGradlePlugin) apply false
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
    tasks.withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }
    tasks.withType<Detekt> {
        // Target version of the generated JVM bytecode. It is used for type resolution.
        this.jvmTarget = "1.8"
    }
}

subprojects {
    apply(plugin = "io.gitlab.arturbosch.detekt")
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
