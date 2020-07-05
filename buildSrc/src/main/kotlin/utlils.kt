import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.kotlin

/**
 * Configures the current project as a Kotlin library by adding the Kotlin `stdlib` as a dependency.
 */
fun Project.kotlinLibrary() {
    dependencies {
        "implementation"(kotlin("stdlib"))
    }
}
