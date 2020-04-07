/*
 * © 2019 Match Group, LLC.
 */

plugins {
    kotlin("jvm")
    `java-gradle-plugin`
    `java-library`
    id("com.gradle.plugin-publish")
}

val VERSION_NAME: String by project
val GROUP: String by project

gradlePlugin {
    plugins {
        create("GitQuery") {
            id = "com.tinder.gitquery"
            implementationClass = "com.tinder.gitquery.GitQueryPlugin"
        }
    }
    dependencies {
        implementation(project(":core"))
        testImplementation(Libs.junit)
        testImplementation(gradleTestKit())
    }
}

pluginBundle {
    website = "https://github.com/Tinder/GitQuery"
    vcsUrl = "https://github.com/Tinder/GitQuery"
    description = "A Gradle plugin to query and sync files in a remote git repo."
    (plugins) {
        "GitQuery" {
            displayName = "Gradle Gitquery plugin"
            tags = listOf("gitquery", "protobuf", "git")
            version = VERSION_NAME
        }
    }
    mavenCoordinates {
        groupId = GROUP
        artifactId = "gitquery-plugin"
        version = VERSION_NAME
    }
}

object Versions {
    const val junit = "4.12"
    const val snakeyaml = "1.25"
}

object Libs {
    const val junit = "junit:junit:${Versions.junit}"
    const val snakeyaml = "org.yaml:snakeyaml:${Versions.snakeyaml}"
}
