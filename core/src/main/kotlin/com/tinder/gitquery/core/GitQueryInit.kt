/*
 * © 2019 Match Group, LLC.
 */

package com.tinder.gitquery.core

import com.tinder.gitquery.core.config.GitQueryConfig
import com.tinder.gitquery.core.utils.insertNested
import com.tinder.gitquery.core.utils.prepareRepo
import com.tinder.gitquery.core.utils.repoHeadRevision
import com.tinder.gitquery.core.utils.sortMap
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.PathMatcher
import java.nio.file.Paths
import java.util.stream.Collectors.toList

/**
 * A utility class to help initialize or update [GitQueryConfig] objects saved in a file.
 */
object GitQueryInit {
    private var verbose = false

    /**
     * Use the initConfig [GitQueryInitConfig] attribute to initialize or update the [GitQueryConfig]
     * and save it to [configFile].
     *
     * @param configFile path to the config file.
     * @param config a yaml file that describe a set of files to fetch/sync from a given repository.
     * @param verbose if true, it will print its operations to standard out.
     */
    fun initConfig(
        configFile: String,
        config: GitQueryConfig,
        verbose: Boolean = false,
        buildDir: String = System.getProperty("user.dir") + "/build"
    ) {
        this.verbose = verbose
        config.validate()
        require(config.initConfig.includeGlobs.isNotEmpty()) {
            "Failed to init config: includeGlobs is empty"
        }

        val actualRepoDirectory = config.getActualRepoPath(buildDir)

        val initConfig = config.initConfig

        prepareRepo(
            config.remote,
            if (initConfig.revision.isEmpty()) config.branch else initConfig.revision,
            actualRepoDirectory,
            verbose = verbose
        )

        config.files = initFiles(
            includeGlobs = initConfig.includeGlobs,
            excludeGlobs = initConfig.excludeGlobs,
            actualRepoDirectory = actualRepoDirectory,
            flatFiles = initConfig.flatFiles,
            revision = initConfig.revision
        ).sortMap()

        config.save(configFile)
        println("GitQuery: init complete: $configFile")
    }

    @Suppress("ComplexMethod")
    private fun initFiles(
        includeGlobs: List<String>,
        excludeGlobs: List<String>,
        actualRepoDirectory: String,
        flatFiles: Boolean,
        revision: String
    ): HashMap<String, Any> {
        val actualRepoPath = Paths.get(actualRepoDirectory)
        var repoHeadRevision = revision.ifEmpty { repoHeadRevision(actualRepoDirectory, verbose = verbose) }
        if (repoHeadRevision.uppercase() == "HEAD") {
            repoHeadRevision = repoHeadRevision(actualRepoDirectory, verbose = verbose)
        }

        val ret = HashMap<String, Any>()

        val excludeMatchers = ArrayList<PathMatcher>(excludeGlobs.size)
        for (excludeGlob in excludeGlobs) {
            excludeMatchers.add(FileSystems.getDefault().getPathMatcher("glob:$excludeGlob"))
        }

        for (glob in includeGlobs) {
            val matcher = FileSystems.getDefault().getPathMatcher("glob:$glob")
            Files.walk(actualRepoPath)
                .collect(toList())
                .filter { it: Path? ->
                    it?.let { path ->
                        val relativePath = Paths.get(path.toString().substringAfter("$actualRepoDirectory/"))
                        val matchesInclude = matcher.matches(path) || matcher.matches(relativePath)
                        var excluded = false
                        if (matchesInclude) {
                            for (excludeMatcher in excludeMatchers) {
                                if (excluded) break
                                excluded = excludeMatcher.matches(path) || excludeMatcher.matches(relativePath)
                            }
                        }
                        matchesInclude && !excluded
                    } ?: false
                }
                .map {
                    if (verbose) {
                        println(it)
                    }
                    val relativePath = it.toString()
                        .substringAfter(actualRepoPath.toString())
                        .substringAfter("/")
                    if (flatFiles) {
                        ret[relativePath] = repoHeadRevision
                    } else {
                        ret.insertNested(relativePath, repoHeadRevision)
                    }
                }
        }
        return ret
    }
}
