/*
 * © 2019 Match Group, LLC.
 */

package com.tinder.gitquery

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * The main plugin class.
 */
class GitQueryPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val extension = project.extensions.create("gitQuery", GitQueryExtension::class.java, project)
        val initExtension = project.extensions.create("gitQueryInit", GitQueryInitExtension::class.java, project)
        project.tasks.register("gitQuery", GitQueryTask::class.java, extension, initExtension)

        project.afterEvaluate {
            if (extension.autoSync) {
                project.tasks.getByName("assemble").dependsOn("gitQuery")
            }
        }
    }
}
