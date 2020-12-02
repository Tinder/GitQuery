package com.tinder.gitquery.core.utils

/**
 * Checks if [path] is already absolute (starts with /), if yes return it, if not, return
 * [prefixPath]/[path]. [prefixPath] defaults to System.getProperty("user.dir").
 * If the path is empty, a blank string is returned.
 */
fun toAbsolutePath(
    path: String,
    prefixPath: String = System.getProperty("user.dir"),
): String {
    return when {
        path.isBlank() -> {
            ""
        }
        path[0] == '/' || prefixPath.isBlank() -> {
            path
        }
        else -> {
            "$prefixPath/$path"
        }
    }
}

/**
 * Check for and create the output folder. Throws exceptions if there are issues.
 */
internal fun prepareOutputDirectory(outputPath: String, cleanOutput: Boolean, verbose: Boolean) {
    println("GitQuery: creating outputPath: $outputPath")

    // Either outputPath exists or we can create it
    check(0 == sh(verbose = verbose, "[ -d $outputPath ] || mkdir -p $outputPath")) {
        "OutputDir: $outputPath not found and couldn't be created"
    }

    // Either outputPath doesn't exist, or we can write to it.
    check(0 == sh(verbose = verbose, "[ ! -d $outputPath ] || [ -w $outputPath ]")) {
        "OutputDir: $outputPath does not have write permission"
    }

    if (cleanOutput) {
        // Clean the output folder so that if files were removed from the config, they don't get included.
        check(0 == sh(verbose = verbose, "(cd $outputPath && rm -rf *)")) {
            "Error cleaning outputDir with path: `$outputPath"
        }
    }
}
