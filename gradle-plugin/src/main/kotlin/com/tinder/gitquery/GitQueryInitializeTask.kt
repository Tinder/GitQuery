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
open class GitQueryInitializeTask @Inject constructor(extension: GitQueryInitializeExtension) :
    GitQuerySyncTask(extension) {

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
            config = readConfig(createIfNotExists = true),
            verbose = verbose
        )
    }

    override fun readConfig(createIfNotExists: Boolean): GitQueryConfig {
        val config = super.readConfig(createIfNotExists = createIfNotExists)
        if (includeGlobs.isNotEmpty()) {
            config.includeGlobs = includeGlobs
        }
        if (excludeGlobs.isNotEmpty()) {
            config.excludeGlobs = excludeGlobs
        }
        config.flatFiles = flatFiles
        return config
    }
}
