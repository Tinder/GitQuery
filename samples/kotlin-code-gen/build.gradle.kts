import com.google.protobuf.gradle.builtins
import com.google.protobuf.gradle.generateProtoTasks
import com.google.protobuf.gradle.id
import com.google.protobuf.gradle.ofSourceSet
import com.google.protobuf.gradle.plugins
import com.google.protobuf.gradle.protobuf
import com.google.protobuf.gradle.protoc
import com.google.protobuf.gradle.remove
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.10"
    kotlin("plugin.serialization") version "1.4.10"
    application
    id("com.google.protobuf") version "0.8.12"
    // Fetch/sync remote proto files prior to code gen.
    id("com.tinder.gitquery") version "2.0.1"
}

val protoDir = "src/main/proto"

gitQuery {
    configFile = "gitquery-proto.yml"
    outputDir = protoDir
    repoDir = "tmp/.gitquery"
    cleanOutput = true
}

sourceSets {
    main {
        proto.srcDirs(protoDir)
    }
}

val kotlinxSerializationVersion by extra("0.20.0")
val protobufVersion by extra("3.12.2")
val pbandkVersion by extra("0.8.1")

repositories {
    jcenter()
    if (System.getenv("CI") == "true") {
        mavenLocal()
    }
    maven("https://jitpack.io")
}

application {
    mainClassName = "pbandk.examples.addressbook.MainKt"
    applicationName = "addressbook"
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:$kotlinxSerializationVersion")
    implementation("com.github.streem.pbandk:pbandk-runtime-jvm:$pbandkVersion")
}

protobuf {
    generatedFilesBaseDir = "$projectDir/src"
    protoc {
        artifact = "com.google.protobuf:protoc:$protobufVersion"
    }
    plugins {
        id("kotlin") {
            artifact = "com.github.streem.pbandk:protoc-gen-kotlin-jvm:$pbandkVersion:jvm8@jar"
        }
    }
    generateProtoTasks {
        ofSourceSet("main").forEach { task ->
            if (task.name == "generateProto") {
                task.dependsOn(tasks.getByName("gitQuery"))
            }
            task.builtins {
                remove("java")
            }
            task.plugins {
                id("kotlin") {
                    option("kotlin_package=pbandk.examples.addressbook.pb")
                }
            }
        }
    }
}

tasks {
    compileJava {
        enabled = false
    }

    withType<KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }
}

// Workaround the Gradle bug resolving multi-platform dependencies.
// Fix courtesy of https://github.com/square/okio/issues/647
configurations.forEach {
    if (it.name.toLowerCase().contains("kapt") || it.name.toLowerCase().contains("proto")) {
        it.attributes.attribute(
            Usage.USAGE_ATTRIBUTE,
            objects.named(Usage::class.java, Usage.JAVA_RUNTIME)
        )
    }
}
