package com.tinder.gitquery.core

import com.tinder.gitquery.core.config.GitQueryConfig
import com.tinder.gitquery.core.config.GitQueryConfigSchema
import com.tinder.gitquery.core.config.GitQueryInitConfig
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class GitQueryInitTest {
    @get:Rule
    val testProjectDir = TemporaryFolder()

    @Before
    fun setup() {
        testProjectDir.create()
        testProjectDir.apply {
            newFolder("gitquery-output")
        }
    }

    @After
    fun tearDown() {
        testProjectDir.delete()
    }

    @Test
    fun `initConfig - given missing remote, should fail`() {
        // When
        val actualError = kotlin.runCatching {
            GitQueryInit.initConfig(
                configFile = "${testProjectDir.root}/gitquery.yml",
                config = GitQueryConfig(
                    schema = GitQueryConfigSchema(version = "1"),
                    branch = "master",
                    files = mapOf(
                        "definitions" to mapOf("user.proto" to "42933446d0321958e8c12216d04b9f0c382ebf1b")
                    ),
                    repoDir = "${testProjectDir.root}/tmp/remote",
                    outputDir = "${testProjectDir.root}/gitquery-output"
                ),
                buildDir = testProjectDir.root.path
            )
        }.exceptionOrNull()

        // Then
        require(actualError is IllegalArgumentException)
        assert(actualError.message == "Parameter remote may not be a blank string ()")

        assert(!File("${testProjectDir.root}/gitquery-output/definitions/user.proto").exists())
        assert(!File("${testProjectDir.root}/gitquery-output/README.md").exists())
    }

    @Test
    fun `initConfig - given missing branch, should fail`() {
        // When
        val actualError = kotlin.runCatching {
            GitQueryInit.initConfig(
                configFile = "${testProjectDir.root}/gitquery.yml",
                config = GitQueryConfig(
                    schema = GitQueryConfigSchema(version = "1"),
                    remote = "https://github.com/aminghadersohi/ProtoExample.git",
                    branch = "",
                    repoDir = "${testProjectDir.root}/tmp/remote",
                    outputDir = "${testProjectDir.root}/gitquery-output"
                ),
                buildDir = testProjectDir.root.path
            )
        }.exceptionOrNull()

        // Then
        require(actualError is IllegalArgumentException)
        assert(actualError.message == "Parameter branch may not be a blank string ()")

        assert(!File("${testProjectDir.root}/gitquery-output/definitions/user.proto").exists())
        assert(!File("${testProjectDir.root}/gitquery-output/README.md").exists())
    }

    @Test
    fun `initConfig - given empty includeGlobs, should fail`() {
        // When
        val actualError = kotlin.runCatching {
            GitQueryInit.initConfig(
                configFile = "${testProjectDir.root}/gitquery.yml",
                config = GitQueryConfig(
                    schema = GitQueryConfigSchema(version = "1"),
                    remote = "https://github.com/aminghadersohi/ProtoExample.git",
                    branch = "master",
                    repoDir = "${testProjectDir.root}/tmp/remote",
                    outputDir = "${testProjectDir.root}/gitquery-output"
                ),
                buildDir = testProjectDir.root.path
            )
        }.exceptionOrNull()

        // Then
        require(actualError is IllegalArgumentException)
        assert(actualError.message == "Failed to init config: includeGlobs is empty")

        assert(!File("${testProjectDir.root}/gitquery-output/definitions/user.proto").exists())
        assert(!File("${testProjectDir.root}/gitquery-output/README.md").exists())
    }

    @Test
    fun `initConfig - given non-existent file, should create file`() {
        // When
        GitQueryInit.initConfig(
            configFile = "${testProjectDir.root}/gitquery.yml",
            config = GitQueryConfig(
                schema = GitQueryConfigSchema(version = "1"),
                remote = "https://github.com/aminghadersohi/ProtoExample.git",
                branch = "master",
                repoDir = "${testProjectDir.root}/tmp/remote",
                outputDir = "${testProjectDir.root}/gitquery-output",
                initConfig = GitQueryInitConfig(
                    includeGlobs = listOf("**/*.proto")
                )
            ),
            buildDir = testProjectDir.root.path
        )

        assert(File("${testProjectDir.root}/gitquery.yml").exists())
    }
}
