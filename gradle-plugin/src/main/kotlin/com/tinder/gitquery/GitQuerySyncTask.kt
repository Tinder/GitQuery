/*
 * © 2019 Match Group, LLC.
 */

package com.tinder.gitquery

import com.tinder.gitquery.core.GitQuerySync
import com.tinder.gitquery.core.config.GitQueryConfig
import com.tinder.gitquery.core.utils.toAbsolutePath
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
 * @param syncExtension [GitQuerySyncExtension]
 */
open class GitQuerySyncTask @Inject constructor(
    syncExtension: GitQuerySyncExtension
) : DefaultTask() {

    /**
     * The relative (to projectDir) path to a yaml file that describe a set of files to fetch/sync from a given
     * repository.
     */
    @Input
    val configFile: String = syncExtension.configFile

    /** The remote repository to query files from. */
    @Input
    val remote: String = syncExtension.remote

    /**
     * The single branch that will be cloned on first run and pulled incrementally on subsequent
     * runs. The revision values used in [commits] and [files] must exist under [branch].
     */
    @Input
    val branch: String = syncExtension.branch

    /** A directory to hold the intermediate cloned git repo. */
    @Input
    val repoDir: String = syncExtension.repoDir

    /** A directory to sync the queried files into. */
    @Input
    val outputDir: String = syncExtension.outputDir

    /** If true (default), cleans out the output folder prior to running sync. */
    @Input
    val cleanOutput: Boolean = syncExtension.cleanOutput

    /** An boolean to enable showing the underlying commands and their outputs in the console. (default: false) */
    @Input
    val verbose: Boolean = syncExtension.verbose

    init {
        group = "build"
        description = "Sync the files listed in the config file"
    }

    @TaskAction
    open fun taskAction() {
        GitQuerySync.sync(
            config = readConfig(createIfNotExists = false),
            verbose = verbose,
            projectDir = project.projectDir.path,
            buildDir = project.buildDir.path
        )
    }

    protected fun readConfig(createIfNotExists: Boolean): GitQueryConfig {
        return GitQueryConfig.load(
            filename = configFileName(),
            createIfNotExists = createIfNotExists
        ).also {
            if (remote.isNotBlank()) {
                it.remote = remote
            }
            if (branch.isNotBlank()) {
                it.branch = branch
            }
            if (repoDir.isNotBlank()) {
                it.repoDir = repoDir
            }
            if (outputDir.isNotBlank()) {
                it.outputDir = outputDir
            }
            it.cleanOutput = cleanOutput
        }
    }

    protected fun configFileName(): String {
        return toAbsolutePath(
            path = configFile,
            prefixPath = "${project.projectDir}"
        )
    }
}
