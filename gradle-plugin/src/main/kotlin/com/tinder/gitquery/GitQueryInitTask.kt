/*
 * © 2019 Match Group, LLC.
 */

package com.tinder.gitquery

import com.tinder.gitquery.core.GitQueryInit.initConfig
import org.gradle.api.logging.LogLevel
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
open class GitQueryInitTask @Inject constructor(
    syncExtension: GitQuerySyncExtension,
    initExtension: GitQueryInitExtension
) : GitQuerySyncTask(syncExtension = syncExtension) {

    /* A list of globs to include when generating the config file. */
    @Input
    val includeGlobs: List<String> = initExtension.includeGlobs

    /* A list of globs to exclude when generating the config file. */
    @Input
    val excludeGlobs: List<String> = initExtension.excludeGlobs

    /*
     * If true (default), when --init-config is used, the files attribute
     * in the resulted saved config file will be a flat map of filename to sha values.
     * If false, it will be a tree of directories as parent nodes and files as leaf nodes.
     */
    @Input
    val flatFiles: Boolean = initExtension.flatFiles

    /*
     * A revision under [branch], when --init-config is applied, that will be used as the default revision for files.
    */
    @Input
    val revision: String = initExtension.revision

    init {
        group = "build"
        description = "Initialize or update the files in the config file based on includeGlobs, and excludeGlobs"
    }

    @TaskAction
    override fun taskAction() {
        if (includeGlobs.isEmpty()) {
            logger.log(LogLevel.WARN, "GitQueryInit skipped - includeGlobs is empty.")
            return
        }
        initConfig(
            configFile = configFileName(),
            config = readConfig(createIfNotExists = true).apply {
                initConfig.includeGlobs = includeGlobs
                if (excludeGlobs.isNotEmpty()) {
                    initConfig.excludeGlobs = excludeGlobs
                }
                if (revision.isNotBlank()) {
                    initConfig.revision = revision
                }
                initConfig.flatFiles = flatFiles
            },
            verbose = verbose,
            buildDir = project.buildDir.path
        )
    }
}
