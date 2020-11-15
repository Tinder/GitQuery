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
        project.tasks.register("gitQuery", GitQuerySyncTask::class.java, syncExtension)

        val initializeExtension = project.extensions
            .create("gitQueryInit", GitQueryInitializeExtension::class.java, project)
        project.tasks.register("gitQueryInit", GitQueryInitializeTask::class.java, initializeExtension)

        project.afterEvaluate {
            if (syncExtension.autoSync) {
                project.tasks.getByName("assemble").dependsOn("gitQuery")
            }
        }
    }
}
