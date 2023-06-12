import com.github.jengelman.gradle.plugins.shadow.ShadowPlugin
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.invoke
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType

fun Project.setupShadowJar() {
    apply<ShadowPlugin>()

    tasks {
        withType<ShadowJar> {
            archiveFileName.set(project.name + "-all.jar")
            minimize()
        }

        getByName("build") {
            dependsOn(withType<ShadowJar>())
        }
    }
}

fun Project.copyToPluginsFolder() {
    tasks {
        register("serverCopy", Copy::class) {
            dependsOn(withType<ShadowJar>())

            from(fileTree("build/libs").include("*-all.jar"))
            into(rootProject.file(".server/plugins"))
        }

        getByName("build") {
            dependsOn(getByName("serverCopy"))
        }
    }
}

private fun ShadowJar.relocate(group: Any, vararg dependencies: String) {
    dependencies.forEach {
        val split = it.split(".")
        val name = split.last()
        relocate(it, "$group.dependencies.$name")
    }
}