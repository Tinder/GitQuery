/*
 * © 2019 Match Group, LLC.
 */

package com.tinder.gitquery

import com.tinder.gitquery.core.GitQueryConfig
import com.tinder.gitquery.core.GitQueryCore
import com.tinder.gitquery.core.GitQueryCore.toAbsolutePath
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

/**
 * A Gradle task that given a yaml file, describing a set of files in a remote git repo, and an
 * intermediate (repo) directory, query, fetch and sync those files into a given output directory.
 *
 * For more details see the README.md file in the root of this project.
 *
 * @param extension [GitQuerySyncExtension]
 */
open class GitQuerySyncTask @Inject constructor(extension: GitQuerySyncExtension) : GitQueryDefaultTask(extension) {

    init {
        group = "build"
        description = "Sync the files listed in the config file"
    }

    @TaskAction
    fun sync() {
        GitQueryCore.sync(config = readConfig(), verbose = verbose)
    }

    private fun readConfig(): GitQueryConfig {
        val config = GitQueryConfig.load(
            filename = configFileName(),
            createIfNotExists = false
        )
        if (remote.isNotBlank()) {
            config.remote = remote
        }
        if (branch.isNotBlank()) {
            config.branch = branch
        }
        config.repoDir = toAbsolutePath(
            path = if (repoDir.isNotBlank()) {
                repoDir
            } else {
                config.repoDir
            },
            prefixPath = "${project.buildDir}"
        )
        config.outputDir = toAbsolutePath(
            path = if (outputDir.isNotBlank()) {
                outputDir
            } else {
                config.outputDir
            },
            prefixPath = "${project.projectDir}"
        )
        config.cleanOutput = cleanOutput
        return config
    }

    private fun configFileName(): String {
        return GitQueryCore.toAbsolutePath(
            path = configFile,
            prefixPath = "${project.projectDir}"
        )
    }
}
