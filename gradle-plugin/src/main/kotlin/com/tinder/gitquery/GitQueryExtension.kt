/*
 * © 2019 Match Group, LLC.
 */

package com.tinder.gitquery

import org.gradle.api.Project

open class GitQueryExtension(val project: Project) {
    var configFile: String = ""
    var outputDir: String = ""
    var repoDir: String = ""
}
