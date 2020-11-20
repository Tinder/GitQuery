/*
 * © 2019 Match Group, LLC.
 */

package com.tinder.gitquery.core

import com.tinder.gitquery.core.config.GitQueryConfig
import com.tinder.gitquery.core.utils.prepareOutputDirectory
import com.tinder.gitquery.core.utils.prepareRepo
import com.tinder.gitquery.core.utils.sh
import com.tinder.gitquery.core.utils.toAbsolutePath

/**
 * A utility class that given a yaml file, describing a set of files in a remote git repo, and an
 * intermediate (repo) directory, query, fetch and sync those files into a given output directory.
 *
 * For more details see the README.md file in the root of this project.
 */
object GitQuerySync {

    /**
     * Sync all files
     *
     * @param config a yaml file that describe a set of files to fetch/sync from a given repository
     * @param verbose if true, it will print its operations to standard out.
     */
    fun sync(
        config: GitQueryConfig,
        verbose: Boolean = false,
    ) {
        config.validate()

        val outputPath = toAbsolutePath(config.outputDir)

        val actualRepoPath = config.getActualRepoPath()

        prepareRepo(config.remote, config.branch, actualRepoPath, verbose = verbose)

        prepareOutputDirectory(
            outputPath = outputPath,
            cleanOutput = config.cleanOutput,
            verbose = verbose
        )

        // Sync all the files in `config.files`, recursively.
        syncFiles(
            fileMap = config.files,
            remote = config.remote,
            commits = config.commits,
            repoPath = actualRepoPath,
            outputPath = outputPath,
            relativePath = "",
            verbose = verbose
        )
    }

    /**
     * Sync all files recursively.
     */
    @Suppress("LongParameterList")
    private fun syncFiles(
        fileMap: Map<String, Any>,
        commits: Map<String, String>,
        remote: String,
        repoPath: String,
        outputPath: String,
        relativePath: String,
        verbose: Boolean,
    ) {
        fileMap.forEach { (filename, value) ->
            val path = if (relativePath.isNotBlank()) "$relativePath/$filename" else filename
            val destDir = "$outputPath/$path"

            // Value is nested map of directories to files and files to the revision for each file.
            when (value) {
                is Map<*, *> -> {
                    @Suppress("UNCHECKED_CAST")
                    syncFiles(
                        commits = commits,
                        fileMap = fileMap[filename] as Map<String, Any>,
                        remote = remote,
                        repoPath = repoPath,
                        outputPath = outputPath,
                        relativePath = path,
                        verbose = verbose
                    )
                }
                // Value is expected to be the git revision for the file
                else -> {
                    var actualRelativePath = path.substringBeforeLast("/")
                    if (actualRelativePath == path) {
                        actualRelativePath = relativePath
                    }
                    val revision = if (commits.containsKey(value)) commits[value] else value
                    // Create the destination directory for each file
                    // `<outputDir>/<definition>/file.proto`,
                    // then run `git show revision:file > dest` to copy the file into the dest
                    val exitCode = sh(
                        verbose = verbose,
                        """
                        (cd $repoPath && mkdir -p $outputPath/$actualRelativePath &&
                        (git show $revision:$path > $destDir))
                        """.trimIndent()
                    )
                    check(exitCode == 0) {
                        "Failed to sync: $remote/$path: exit code=$exitCode"
                    }
                }
            }
        }
    }
}
