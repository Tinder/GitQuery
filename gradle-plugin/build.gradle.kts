/*
 * © 2019 Match Group, LLC.
 */

plugins {
    kotlin("jvm")
    `java-gradle-plugin`
    `java-library`
     id("com.vanniktech.maven.publish")
}

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

object Versions {
    const val junit = "4.12"
    const val snakeyaml = "1.25"
}

object Libs {
    const val junit = "junit:junit:${Versions.junit}"
    const val snakeyaml = "org.yaml:snakeyaml:${Versions.snakeyaml}"
}
