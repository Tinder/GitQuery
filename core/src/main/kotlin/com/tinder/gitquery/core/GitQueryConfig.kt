/*
 * © 2019 Match Group, LLC.
 */

package com.tinder.gitquery.core

import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.Constructor
import org.yaml.snakeyaml.representer.Representer
import java.io.FileWriter
import java.nio.file.Files
import java.nio.file.Paths
import java.util.Collections.emptyMap

const val DEFAULT_AUTO_SYNC: Boolean = false
const val DEFAULT_BRANCH: String = "master"
const val DEFAULT_CLEAN_OUTPUT: Boolean = true
const val DEFAULT_CONFIG_FILENAME: String = "gitquery.yml"
const val DEFAULT_GRADLE_REPO_DIR: String = "tmp/gitquery/repo"
const val DEFAULT_FLAT_FILES: Boolean = false
const val DEFAULT_OUTPUT_DIR: String = "gitquery-output"
const val DEFAULT_REMOTE: String = ""
const val DEFAULT_REPO_DIR: String = "/tmp/gitquery/repo"
const val DEFAULT_SHA: String = ""
const val DEFAULT_VERBOSE: Boolean = false
val defaultExcludeGlobs: List<String> = emptyList()
val defaultIncludeGlobs: List<String> = emptyList()

/**
 * A model for the config yaml file.
 */
data class GitQueryConfig(
    /* The version of the schema for this config. */
    var schema: GitQueryConfigSchema = GitQueryConfigSchema(),
    /* The remote repository to query files from. */
    var remote: String = DEFAULT_REMOTE,
    /*
    The single branch that will be cloned on first run and pulled incrementally on subsequent
    runs. The sha values used in [commits] and [files] must be available under [branch].
    */
    var branch: String = DEFAULT_BRANCH,
    /*
    Specify a nested map of filenames to sha (or commit alias) included file that we
    want to query and sync. The structure of [files] matches the directory structure of the
    remote repo. A key whose value is a nested map is considered a directory.
    */
    var files: Map<String, Any> = emptyMap(),
    /* A list of commit aliases that can be used in the [files] section. */
    var commits: Map<String, String> = emptyMap(),
    /* A directory to hold the intermediate cloned git repo. */
    var repoDir: String = DEFAULT_REPO_DIR,
    /* A directory to sync the queried files into. */
    var outputDir: String = DEFAULT_OUTPUT_DIR,
    /* If true [default], cleans out the output folder prior to running sync. */
    var cleanOutput: Boolean = DEFAULT_CLEAN_OUTPUT,
    /*
    If true [default], when --init-config is used, the files attribute
    in the resulted saved config file will be a flat map of filename to sha values.
    If false, it will be a tree of directories as parent nodes and files as leaf nodes.
    */
    var flatFiles: Boolean = DEFAULT_FLAT_FILES,
    /* A list of globs to include when generating the config file. */
    var includeGlobs: List<String> = defaultIncludeGlobs,
    /* A list of globs to exclude when generating the config file. */
    var excludeGlobs: List<String> = defaultExcludeGlobs,
    /* A map of String -> Any - enabling self contained integration with various systems. */
    var extra: Map<String, Any> = emptyMap(),
) {
    /**
     * Save the config to a file.
     *
     * @param filename to path to save to.
     */
    fun save(filename: String) {
        val writer = FileWriter(filename)
        val options = DumperOptions()
        options.defaultFlowStyle = DumperOptions.FlowStyle.BLOCK
        val yaml = Yaml(Constructor(GitQueryConfig::class.java), Representer(options), options)
        yaml.dump(this, writer)
        writer.flush()
    }

    /**
     * using the repoDir and remote config attributes, return where the repo should be cloned.
     */
    fun getActualRepoPath(): String {
        val repoPath = GitQueryCore.toAbsolutePath(this.repoDir)
        val repoName = remote
            .substring(remote.lastIndexOf("/") + 1)
            .removeSuffix(".git")
        return "$repoPath/$repoName"
    }

    /**
     * Validate essential config attributes.
     */
    fun validate() {
        require(remote.isNotBlank()) {
            "Parameter remote may not be a blank string ($remote)"
        }

        require(branch.isNotBlank()) {
            "Parameter branch may not be a blank string ($branch)"
        }
    }

    companion object {
        /**
         * Load the config file.
         *
         * @param filename the path to the config file
         * @param createIfNotExists if the filename doesn't exist, create it with default attribute values.
         */
        fun load(filename: String, createIfNotExists: Boolean): GitQueryConfig {
            require(filename.isNotBlank()) {
                "Input filename may not be a blank string ($filename)"
            }

            val filePath = Paths.get(filename)
            if (!Files.exists(filePath) && createIfNotExists) {
                GitQueryConfig().save(filename)
            }
            check(Files.exists(Paths.get(filename))) {
                "Config file does not exist ($filename)"
            }

            val yaml = Yaml(Constructor(GitQueryConfig::class.java))
            return Files
                .newBufferedReader(filePath)
                .use {
                    yaml.load(it)
                }
        }
    }
}
