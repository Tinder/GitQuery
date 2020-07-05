/*
 * © 2019 Match Group, LLC.
 */

package com.tinder.gitquery.core

import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.Constructor
import java.io.File
import java.nio.file.Files
import java.util.Collections.emptyMap

/**
 * A model for the config yaml file.
 */
data class GitQueryConfig(
    // The version of the schema for this config.
    var schema: GitQueryConfigSchema = GitQueryConfigSchema(),
    // The remote repository to query files from.
    var remote: String = "",
    // The single branch that will be cloned on first run and pulled incrementally on subsequent
    // runs. The sha values used in [commits] and [files] must be available under [branch].
    var branch: String = "",
    // Specify a nested map of filenames to sha (or commit alias) included file that we
    // want to query and sync. The structure of [files] matches the directory structure of the
    // remote repo. A key whose value is a nested map is considered a directory.
    var files: Map<String, Any> = emptyMap(),
    // A list of commit aliases that can be used in the [files] section.
    var commits: Map<String, String> = emptyMap(),
    // A directory to hold the intermediate cloned git repo.
    var repoDir: String = defaultRepoDir,
    // A directory to sync the queried files into.
    var outputDir: String = defaultOutputDir,
    // If true [default], cleans out the output folder prior to running sync.
    var cleanOutput: Boolean = false,
    // A map of String -> Any - enabling self contained integration with various systems.
    var extra: Map<String, Any> = emptyMap()
)

/**
 * Load the config file.
 *
 * @param filename the path to the config file
 */
fun loadConfig(filename: String): GitQueryConfig {
    require(filename.isNotBlank()) {
        "Input filename may not be a blank string ($filename)"
    }

    val file = File(filename)
    check(file.exists()) {
        "Config file does not exist ($filename)"
    }

    val yaml = Yaml(Constructor(GitQueryConfig::class.java))
    return Files.newBufferedReader(file.toPath()).use {
        yaml.load(it)
    }
}

/**
 * Validate essential config attributes.
 */
fun GitQueryConfig.validate() {
    require(remote.isNotBlank()) {
        "Parameter remote may not be a blank string ($remote)"
    }

    require(branch.isNotBlank()) {
        "Parameter branch may not be a blank string ($branch)"
    }
}
