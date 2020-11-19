/*
 * © 2019 Match Group, LLC.
 */

package com.tinder.gitquery.core.utils

/**
 * Utility methods related to a Git repository.
 */

/**
 * Clone or pull the `remote` repo @ `branch` into file(`repoDir`)
 *
 * @param remote the url to the remote git repo
 * @param branch a branch in the remote repo
 * @param repoDir where the remote repo(s) can be cloned locally and stored temporarily.
 */
internal fun prepareRepo(remote: String, branch: String, repoDir: String, verbose: Boolean) {
    val repoExists = repoExists(repoDir, verbose = verbose)
    var exitCode = 0

    // If repo directory already exists, it means we have already cloned the remote repo
    if (repoExists) {
        // Since we have th repo already, fetch the right branch from origin and checkout the branch
        // In cases where the branch changes, `git checkout $branch` will fail silently, prompting
        // us to do a clean single branch clone of the repository.
        exitCode = sh(
            verbose = verbose,
            "cd $repoDir && git checkout $branch &>/dev/null && git pull origin $branch --tags"
        )
    }

    // Either:
    // 1) repoDir doesn't exist
    // 2) or, we couldn't pull the branch that we wanted.
    // Cleanup and try a fresh clone.
    if (exitCode != 0 || !repoExists) {
        sh(verbose = verbose, "rm -rf $repoDir")
        exitCode = sh(verbose = verbose, "git clone --single-branch -b $branch $remote $repoDir")
    }

    check(exitCode == 0) { "Error cloning/updating repo $remote into directory $repoDir" }
}

/**
 * Get current revision of the repoDir.
 */
internal fun repoHeadRevision(repoDir: String, verbose: Boolean): String {
    return shellResult(verbose = verbose, "(cd $repoDir && git rev-parse HEAD)")
}

/**
 * Checks the existence of the repoDir
 */
internal fun repoExists(repoDir: String, verbose: Boolean): Boolean {
    return 0 == sh(verbose = verbose, "[ -d $repoDir ] && [ -d $repoDir/.git ]")
}
