package com.tinder.gitquery.core

/**
 * Run a shell command.
 */
internal fun sh(verbose: Boolean = false, vararg cmd: String): Int =
    runCommand(verbose = verbose, "sh", "-c", *cmd)

internal fun runCommand(verbose: Boolean = false, vararg cmd: String): Int {
    val processBuilder = ProcessBuilder()
        .command(*cmd)
        .redirectErrorStream(true)

    if (verbose) {
        println(processBuilder.command().toString())
    }

    val process = processBuilder.start()
    val lines = process.inputStream.bufferedReader().use { reader ->
        reader.readLines().joinToString(separator = "\n")
    }

    val exitCode = process.waitFor()
    if (verbose || (exitCode != 0 && lines.isNotEmpty())) {
        println(processBuilder.command().toString())
        println("exit code = $exitCode")
        println(lines)
    }
    return exitCode
}

/**
 * Run a shell command for a value.
 */
internal fun shellResult(verbose: Boolean = false, vararg cmd: String): String =
    runCommandAndGetResult(verbose = verbose, "sh", "-c", *cmd)

private fun runCommandAndGetResult(verbose: Boolean = false, vararg cmd: String): String {
    val processBuilder = ProcessBuilder()
        .command(*cmd)
        .redirectErrorStream(false)

    if (verbose) {
        println(processBuilder.command().toString())
    }

    val process = processBuilder.start()
    val lines = process.inputStream.bufferedReader().use { reader ->
        reader.readLines().joinToString(separator = "\n")
    }

    val exitCode = process.waitFor()
    if ((exitCode != 0 && lines.isNotEmpty())) {
        println(processBuilder.command().toString())
        println("exit code = $exitCode")
        println(lines)
    }
    return lines
}
