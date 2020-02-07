/*
 * © 2019 Match Group, LLC.
 */

package com.tinder.gitquery.e2e

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class GitQueryTaskTest {

    @get:Rule
    val testProjectDir = TemporaryFolder()

    @Before
    fun setup() {
        testProjectDir.create()
    }

    @After
    fun tearDown() {
        testProjectDir.delete()
    }

    @Test
    fun taskCreateFolderWithProtoFiles() {
        testProjectDir.apply {
            newFolder("synced-src")
            newFile("build.gradle").appendText(getBuildGradleSetup())
            newFile("config.yml").appendText(getContentConfig())
        }

        val result = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withArguments("gitQueryTask")
            .withPluginClasspath()
            .build()
        assert(result.task(":gitQueryTask")?.outcome == TaskOutcome.SUCCESS)
        assert(result.output.contains("Creating outputPath"))
        assert(File("${testProjectDir.root}/synced-src/definitions/user.proto").exists())
    }


    @Test
    fun missingRemoteInConfigFileIsThrowingException() {
        testProjectDir.apply {
            newFolder("synced-src")
            newFile("build.gradle").appendText(getBuildGradleSetup())
            newFile("config.yml").appendText(getContentMissingRemoteConfig())
        }

        val result = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withArguments("gitQueryTask")
            .withPluginClasspath()
            .buildAndFail()
        println(result.output)
        assert(result.task(":gitQueryTask")?.outcome == TaskOutcome.FAILED)
        assert(result.output.contains("Parameter remote can't be an empty string ()"))
    }


    @Test
    fun wrongConfigFormatThrowingException() {
        testProjectDir.apply {
            newFolder("synced-src")
            newFile("build.gradle").appendText(getBuildGradleSetup())
            newFile("config.yml").appendText(getContentWrongFormat())
        }


        val result = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withArguments("gitQueryTask")
            .withPluginClasspath()
            .buildAndFail()

        assert(result.output.contains("Can't construct a java object for tag:yaml.org"))
        assert(result.task(":gitQueryTask")?.outcome == TaskOutcome.FAILED)


    }

    private fun getBuildGradleSetup() = """
plugins {
  id 'com.tinder.gitquery'
}

gitQuery {
    configFile =  "config.yml"
    outputDir =  "synced-src"
    repoDir = "tmp/remote"
}""".trimIndent()


    private fun getContentConfig() = """
---
schema:
  version: 1
remote: https://github.com/cdsap/ProtoExample.git
branch: master
definitions:
  definitions:
    user.proto: 42933446d0321958e8c12216d04b9f0c382ebf1b
""".trimIndent()

    private fun getContentMissingRemoteConfig() = """
---
schema:
  version: 1
branch: master
definitions:
  definitions:
    user.proto: 42933446d0321958e8c12216d04b9f0c382ebf1b
""".trimIndent()

    private fun getContentWrongFormat() = """---
master{ }
incorrectFormat
 """.trimIndent()
}
