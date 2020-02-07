/*
 * © 2019 Match Group, LLC.
 */

package com.tinder.gitquery

import org.gradle.api.Plugin
import org.gradle.api.Project

class GitQueryPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val extension = target.extensions.create("gitQuery", GitQueryExtension::class.java, target)
        target.tasks.register("gitQueryTask", GitQueryTask::class.java, extension)
    }
}
