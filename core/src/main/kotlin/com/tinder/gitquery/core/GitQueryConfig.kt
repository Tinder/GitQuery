/*
 * © 2019 Match Group, LLC.
 */

package com.tinder.gitquery.core

import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.Constructor
import org.yaml.snakeyaml.representer.Representer
import java.io.File
import java.io.FileWriter
import java.nio.file.Files
import java.util.Collections.emptyMap


/**
 * A model for the config yaml file.
 */
data class GitQueryConfig(
    /* The version of the schema for this config. */
    var schema: GitQueryConfigSchema = GitQueryConfigSchema(),
    /* The remote repository to query files from. */
    var remote: String = defaultRemote,
    /*
    The single branch that will be cloned on first run and pulled incrementally on subsequent
    runs. The sha values used in [commits] and [files] must be available under [branch].
    */
    var branch: String = defaultBranch,
    /*
    Specify a nested map of filenames to sha (or commit alias) included file that we
    want to query and sync. The structure of [files] matches the directory structure of the
    remote repo. A key whose value is a nested map is considered a directory.
    */
    var files: Map<String, Any> = emptyMap(),
    /* A list of commit aliases that can be used in the [files] section. */
    var commits: Map<String, String> = emptyMap(),
    /* A directory to hold the intermediate cloned git repo. */
    var repoDir: String = defaultRepoDir,
    /* A directory to sync the queried files into. */
    var outputDir: String = defaultOutputDir,
    /* If true [default], cleans out the output folder prior to running sync. */
    var cleanOutput: Boolean = defaultCleanOutput,
//    /*
//    If false [default], when generate-globs is used, the files attribute
//    in the generated config file will be a flat map of filename to sha.
//    If true, it will be a nested map of files.
//    */
//    var nestedOutput: Boolean = defaultNestedOutput,
    /* A list of globs to include when generating the config file. */
    var includeGlobs: List<String> = defaultIncludeGlobs,
    /* A list of globs to exclude when generating the config file. */
    var excludeGlobs: List<String> = defaultExcludeGlobs,
    /* A map of String -> Any - enabling self contained integration with various systems. */
    var extra: Map<String, Any> = emptyMap()
) {
    companion object {
        /**
         * Load the config file.
         *
         * @param filename the path to the config file
         */
        fun load(filename: String, createIfNotExists: Boolean = false): GitQueryConfig {
            require(filename.isNotBlank()) {
                "Input filename may not be a blank string ($filename)"
            }

            val file = File(filename)
            if (!file.exists() && createIfNotExists) {
                GitQueryConfig().save(file)
            }
            check(file.exists()) {
                "Config file does not exist ($filename)"
            }

            val yaml = Yaml(Constructor(GitQueryConfig::class.java))
            return Files.newBufferedReader(file.toPath()).use {
                yaml.load(it)
            }
        }
    }
}

/**
 * Save the config to a file.
 *
 * @param file to save it to.
 */
fun GitQueryConfig.save(file: File) {
    val writer = FileWriter(file)
    val options = DumperOptions()
    options.defaultFlowStyle = DumperOptions.FlowStyle.BLOCK
    val yaml = Yaml(Constructor(GitQueryConfig::class.java), Representer(options), options)
    yaml.dump(this, writer)
    writer.flush()
}

/**
 * using the repoDir and remote config attributes, return where the repo should be cloned.
 */
fun GitQueryConfig.getActualRepoPath(): String {
    val repoPath = GitQueryCore.toAbsolutePath(this.repoDir)
    val repoName = this.remote
        .substring(this.remote.lastIndexOf("/") + 1)
        .removeSuffix(".git")
    return "$repoPath/$repoName"
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
