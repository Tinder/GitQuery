/*
 * © 2019 Match Group, LLC.
 */

package com.tinder.gitquery

import com.tinder.gitquery.core.GitQueryConfig
import com.tinder.gitquery.core.GitQueryCore
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

/**
 *
 * @param extension [GitQueryInitializeExtension]
 */
open class GitQueryInitializeTask @Inject constructor(extension: GitQueryInitializeExtension) : GitQueryDefaultTask(extension) {

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

    /*
    A sha under [branch], when --init-config is applied, that will be used as the default sha for files..
    */
    @Input
    val sha: String = extension.sha

    init {
        group = "build"
        description = "Initialize or update a config file using include and exclude file globs."
    }

    @TaskAction
    fun initialize() {
        GitQueryCore.initializeConfig(
            configFile = configFileName(),
            config = readConfig(),
            sha = sha,
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
