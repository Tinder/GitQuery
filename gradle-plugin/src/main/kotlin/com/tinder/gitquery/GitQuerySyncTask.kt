/*
 * © 2019 Match Group, LLC.
 */

package com.tinder.gitquery

import com.tinder.gitquery.core.GitQueryConfig
import com.tinder.gitquery.core.GitQueryCore
import com.tinder.gitquery.core.GitQueryCore.toAbsolutePath
import com.tinder.gitquery.core.defaultCleanOutput
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
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
open class GitQuerySyncTask @Inject constructor(extension: GitQuerySyncExtension) : DefaultTask() {

    /*
    The relative (to projectDir) path to a yaml file that describe a set of files to fetch/sync from a given
    repository.
    */
    @Input
    val configFile: String = extension.configFile

    /* The remote repository to query files from. */
    @Input
    val remote: String = extension.remote

    /*
    The single branch that will be cloned on first run and pulled incrementally on subsequent
    runs. The sha values used in [commits] and [files] must be available under [branch].
    */
    @Input
    val branch: String = extension.branch

    /* A directory to hold the intermediate cloned git repo. */
    @Input
    val repoDir: String = extension.repoDir

    /* A directory to sync the queried files into. */
    @Input
    val outputDir: String = extension.outputDir

    /* If true [default], cleans out the output folder prior to running sync. */
    @Input
    val cleanOutput: Boolean = extension.cleanOutput

    /* An boolean to enable showing the underlying commands and their outputs in the console. (default: false) */
    @Input
    val verbose: Boolean = extension.verbose

    init {
        group = "build"
        description = "Sync the files listed in the config file"
    }

    @TaskAction
    fun sync() {
        GitQueryCore.sync(config = readConfig(), verbose = verbose)
    }

    protected fun configFileName(): String {
        return toAbsolutePath(
            path = configFile,
            prefixPath = "${project.projectDir}"
        )
    }

    protected open fun readConfig() : GitQueryConfig {
         val config = GitQueryConfig.load(configFileName())
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
}
