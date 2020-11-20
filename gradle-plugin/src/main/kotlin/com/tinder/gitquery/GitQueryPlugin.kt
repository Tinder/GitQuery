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
        val syncExtension = project.extensions.create("gitQuery", GitQuerySyncExtension::class.java, project)
        val initExtension = project.extensions.create("gitQueryInit", GitQueryInitExtension::class.java, project)

        project.tasks.register("gitQuery", GitQuerySyncTask::class.java, syncExtension)
        project.tasks.register("gitQueryInit", GitQueryInitTask::class.java, syncExtension, initExtension)

        project.afterEvaluate {
            if (syncExtension.autoSync) {
                project.tasks.getByName("assemble").dependsOn("gitQuery")
            }
        }
    }
}
