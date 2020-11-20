package com.tinder.gitquery.core

import com.tinder.gitquery.core.config.GitQueryConfig
import com.tinder.gitquery.core.config.GitQueryConfigSchema
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import java.lang.IllegalStateException

class GitQuerySyncTest {
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
    fun `sync - given valid parameters, should succeed`() {
        // When
        GitQuerySync.sync(
            config = GitQueryConfig(
                schema = GitQueryConfigSchema(version = "1"),
                remote = "https://github.com/aminghadersohi/ProtoExample.git",
                branch = "master",
                commits = mapOf("latest" to "d654b510d2689e8ee56d23d03dff2be742737f86"),
                files = mapOf(
                    "README.md" to "latest",
                    "definitions" to mapOf("user.proto" to "42933446d0321958e8c12216d04b9f0c382ebf1b")
                ),
                repoDir = "${testProjectDir.root}/tmp/remote",
                outputDir = "${testProjectDir.root}/gitquery-output"
            )
        )

        // Then
        assert(File("${testProjectDir.root}/gitquery-output/definitions/user.proto").exists())
        assert(File("${testProjectDir.root}/gitquery-output/README.md").exists())
    }

    @Test
    fun `sync - given missing remote, should fail`() {
        // When
        val actualError = kotlin.runCatching {
            GitQuerySync.sync(
                config = GitQueryConfig(
                    schema = GitQueryConfigSchema(version = "1"),
                    branch = "master",
                    files = mapOf(
                        "definitions" to mapOf("user.proto" to "42933446d0321958e8c12216d04b9f0c382ebf1b")
                    ),
                    repoDir = "${testProjectDir.root}/tmp/remote",
                    outputDir = "${testProjectDir.root}/gitquery-output"
                )
            )
        }.exceptionOrNull()

        // Then
        require(actualError is IllegalArgumentException)
        assert(actualError.message == "Parameter remote may not be a blank string ()")

        assert(!File("${testProjectDir.root}/gitquery-output/definitions/user.proto").exists())
        assert(!File("${testProjectDir.root}/gitquery-output/README.md").exists())
    }

    @Test
    fun `sync - given missing branch, should fail`() {
        // When
        val actualError = kotlin.runCatching {
            GitQuerySync.sync(
                config = GitQueryConfig(
                    schema = GitQueryConfigSchema(version = "1"),
                    remote = "https://github.com/aminghadersohi/ProtoExample.git",
                    branch = "",
                    files = mapOf(
                        "definitions" to mapOf("user.proto" to "42933446d0321958e8c12216d04b9f0c382ebf1b")
                    ),
                    repoDir = "${testProjectDir.root}/tmp/remote",
                    outputDir = "${testProjectDir.root}/gitquery-output"
                )
            )
        }.exceptionOrNull()

        // Then
        require(actualError is IllegalArgumentException)
        assert(actualError.message == "Parameter branch may not be a blank string ()")

        assert(!File("${testProjectDir.root}/gitquery-output/definitions/user.proto").exists())
        assert(!File("${testProjectDir.root}/gitquery-output/README.md").exists())
    }

    @Test
    fun `sync - given non-existent file, should fail`() {
        // When
        val actualError = kotlin.runCatching {
            GitQuerySync.sync(
                config = GitQueryConfig(
                    schema = GitQueryConfigSchema(version = "1"),
                    remote = "https://github.com/aminghadersohi/ProtoExample.git",
                    branch = "master",
                    files = mapOf(
                        "definitions" to mapOf("notexist.proto" to "42933446d0321958e8c12216d04b9f0c382ebf1b")
                    ),
                    repoDir = "${testProjectDir.root}/tmp/remote",
                    outputDir = "${testProjectDir.root}/gitquery-output"
                )
            )
        }.exceptionOrNull()

        // Then
        require(actualError is IllegalStateException)
        assert(
            actualError.message == "Failed to sync: " +
                "https://github.com/aminghadersohi/ProtoExample.git/definitions/notexist.proto: exit code=128"
        )
    }
}
