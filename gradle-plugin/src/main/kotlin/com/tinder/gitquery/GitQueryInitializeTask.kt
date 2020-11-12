/*
 * © 2019 Match Group, LLC.
 */

package com.tinder.gitquery

import com.tinder.gitquery.core.GitQueryConfig
import com.tinder.gitquery.core.GitQueryCore
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

/**
 *
 * @param extension [GitQueryInitializeExtension]
 */
open class GitQueryInitializeTask @Inject constructor(extension: GitQueryInitializeExtension) : DefaultTask() {

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

    /* A list of globs to include when generating the config file. */
    @Input
    val includeGlobs: List<String> = extension.includeGlobs

    /* A list of globs to exclude when generating the config file. */
    @Input
    val excludeGlobs: List<String> = extension.excludeGlobs

    /*
    If true [default], when --init-config is used, the files attribute
    in the resulted saved config file will be a flat map of filename to sha values.
    If false, it will be a tree of directories as parent nodes and files as leaf nodes.
    */
    @Input
    val flatFiles: Boolean = extension.flatFiles

    init {
        group = "build"
        description = "Initialize or update a config file using include and exclude file globs."
    }

    @TaskAction
    fun initialize() {
        GitQueryCore.initializeConfig(
            configFile = configFileName(),
            config = readConfig(),
            verbose = verbose
        )
    }

    private fun readConfig(): GitQueryConfig {
        val config = GitQueryConfig.load(configFileName(), createIfNotExists = true)
        if (remote.isNotBlank()) {
            config.remote = remote
        }
        if (branch.isNotBlank()) {
            config.branch = branch
        }
        config.repoDir = GitQueryCore.toAbsolutePath(
            path = if (repoDir.isNotBlank()) {
                repoDir
            } else {
                config.repoDir
            },
            prefixPath = "${project.buildDir}"
        )
        config.outputDir = GitQueryCore.toAbsolutePath(
            path = if (outputDir.isNotBlank()) {
                outputDir
            } else {
                config.outputDir
            },
            prefixPath = "${project.projectDir}"
        )
        config.cleanOutput = cleanOutput

        if (includeGlobs.isNotEmpty()) {
            config.includeGlobs = includeGlobs
        }
        if (excludeGlobs.isNotEmpty()) {
            config.excludeGlobs = excludeGlobs
        }
        config.flatFiles = flatFiles
        return config
    }

    private fun configFileName(): String {
        return GitQueryCore.toAbsolutePath(
            path = configFile,
            prefixPath = "${project.projectDir}"
        )
    }
}
