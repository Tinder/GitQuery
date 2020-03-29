/*
 * © 2019 Match Group, LLC.
 */

package com.tinder.gitquery.core

import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.Constructor
import java.io.File
import java.nio.file.Files

/**
 * A utility class that given a yaml file, describing a set of files in a remote git repo, and an
 * intermediate (repo) directory, query, fetch and sync those files into a given output directory.
 *
 * For more details see the README.md file in the root of this project.
 */
object GitQueryCore {

    /**
     * Sync all files.
     *
     * @param configFile a yaml file that describe a set of files to fetch/sync from a given repository
     * @param repoPath where the remote repo(s) can be cloned locally and stored temporarily.
     * @param outputPath path to a directory where the files should be synced to.
     */
    fun sync(configFile: String, repoPath: String, outputPath: String) {
        val config = loadConfig(configFile)
        validateConfig(config.remote, config.branch)

        val actualRepoPath = "$repoPath/" +
                config.remote.substring(config.remote.lastIndexOf("/")).removeSuffix(".git")

        prepareRepo(config.remote, config.branch, actualRepoPath)

        prepareOutputDirectory(outputPath)

        // Sync all the files in `config.files`, recursively.
        syncFiles(
            fileMap = config.files,
            remote = config.remote,
            commits = config.commits,
            repoPath = actualRepoPath,
            outputPath = outputPath,
            relativePath = ""
        )
    }

    /**
     * Load the config file.
     *
     * @param configFile the path to the config file
     */
    private fun loadConfig(configFile: String): GitQueryConfig {
        require(configFile.isNotEmpty()) {
            "Input configFile can't be an empty string ($configFile)"
        }

        check(0 == sh("[ -f $configFile ]")) {
            "Config file does not exist ($configFile)"
        }

        val yaml = Yaml(Constructor(GitQueryConfig::class.java))
        return Files.newBufferedReader(File(configFile).toPath()).use {
            yaml.load(it)
        }
    }

    /**
     * Validate essential config attributes.
     *
     * @param remote the url to the remote git repo
     * @param branch a branch in the remote repo
     */
    private fun validateConfig(remote: String, branch: String) {
        require(remote.isNotEmpty()) {
            "Parameter remote can't be an empty string ($remote)"
        }

        require(branch.isNotEmpty()) {
            "Parameter branch can't be an empty string ($branch)"
        }
    }

    /**
     * Clone or pull the `remote` repo @ `branch` into file(`repoDir`)
     *
     * @param remote the url to the remote git repo
     * @param branch a branch in the remote repo
     * @param repoDir where the remote repo(s) can be cloned locally and stored temporarily.
     */
    private fun prepareRepo(remote: String, branch: String, repoDir: String) {
        val repoExists = repoExists(repoDir)
        var exitCode = 0

        // If repo directory already exists, it means we have already cloned the remote repo
        if (repoExists) {
            // since we have th repo already, fetch the right branch from origin and checkout the branch
            exitCode =
                sh("cd $repoDir && git checkout $branch && git pull origin $branch")
        }

        // Either:
        // 1) repoDir doesn't exist
        // 2) or, we couldn't pull the branch that we wanted.
        // Cleanup and try a fresh clone.
        if (exitCode != 0 || !repoExists) {
            sh("rm -rf $repoDir")
            exitCode =
                sh("git clone --single-branch -b $branch $remote $repoDir")
        }

        check(exitCode == 0) { "Error cloning/updating repo $remote into directory $repoDir" }
    }

    /**
     * Sync all files recursively.
     */
    private fun syncFiles(
        fileMap: Map<String, Any>,
        commits: Map<String, String>,
        remote: String,
        repoPath: String,
        outputPath: String,
        relativePath: String
    ) {
        fileMap.forEach { (filename, value) ->
            val path = if (relativePath.isNotBlank()) "$relativePath/$filename" else filename
            val destDir = "$outputPath/$path"

            // Value is a map of filenames and directory names to shas
            if (value is Map<*, *>) {
                @Suppress("UNCHECKED_CAST")
                syncFiles(
                    commits = commits,
                    fileMap = fileMap[filename] as Map<String, Any>,
                    remote = remote,
                    repoPath = repoPath,
                    outputPath = outputPath,
                    relativePath = path
                )
            }
            // Value is expected to be the git sha
            else {
                val sha = if (commits.containsKey(value)) commits[value] else value
                // Create the destination directory for each file
                // `<outputDir>/<definition>/file.proto`,
                // then run `git show sha:file > dest` to copy the file into the dest
                val exitCode = sh(
                    """
                        (cd $repoPath && mkdir -p $outputPath/$relativePath && 
                        echo "// DOT NOT EDIT" > $destDir && 
                        echo "// This file is synced from remote repo:" >> $destDir && 
                        echo "// $remote/$path@sha=$sha" >> $destDir && 
                        echo "" >> $destDir && 
                        git show $sha:$path >> $destDir)
                    """.trimIndent()
                )
                check(exitCode == 0) { "Failed to sync: $remote/$path: exit code=$exitCode" }
            }
        }
    }

    /**
     * Check for and create the output folder - relative to projectDir. Throws exceptions if there are issues.
     */
    private fun prepareOutputDirectory(outputPath: String) {
        println("GitQuery: creating outputPath: $outputPath")

        // Either outputPath exists or we can create it
        check(0 == sh("[ -d $outputPath ] || mkdir -p $outputPath")) {
            "OutputDir: $outputPath not found and couldn't be created"
        }

        // Either outputPath doesn't exist, or we can write to it.
        check(0 == sh("[ ! -d $outputPath ] || [ -w $outputPath ]")) {
            "OutputDir: $outputPath does not have write permission"
        }

        // Clean the output folder so that if files were removed from the config, they don't get included.
        check(0 == sh("(cd $outputPath && rm -rf *)")) {
            "Error cleaning outputDir with path: `$outputPath"
        }
    }

    /**
     * Checks the existence of the repoDir
     */
    private fun repoExists(repoDir: String): Boolean {
        return 0 == sh("[ -d $repoDir ] && [ -d $repoDir/.git ]")
    }

    /**
     * Run a shell command.
     */
    private fun sh(vararg cmd: String): Int {
        return runCommand(
            *listOf(
                "sh",
                "-c",
                *cmd
            ).toTypedArray()
        )
    }

    private fun runCommand(vararg cmd: String): Int {
        val processBuilder = ProcessBuilder()
            .command(*cmd)
            .redirectErrorStream(true)

        val process = processBuilder.start()
        val lines = process.inputStream.bufferedReader().use { reader ->
            reader.readLines().joinToString(separator = "\n")
        }

        val exitCode = process.waitFor()
        if (exitCode != 0 && lines.isNotEmpty()) {
            println(lines)
        }
        return exitCode
    }
}

