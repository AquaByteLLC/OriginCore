import com.github.jengelman.gradle.plugins.shadow.ShadowPlugin
import com.github.jengelman.gradle.plugins.shadow.tasks.ConfigureShadowRelocation
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.invoke
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType
import org.gradle.language.jvm.tasks.ProcessResources

fun Project.setupShadowJar() {
    apply<ShadowPlugin>()

    tasks {
        register<ConfigureShadowRelocation>("relocate") {
            target = withType(ShadowJar::class).getByName("shadowJar")

            val arr = project.group.toString().split(".")
            prefix = arr.subList(0, arr.size - 1).joinToString(".") + ".shade"

            doLast {
                synchronized(System.out) {
                    println(">> Relocating shaded packages to " + prefix + ".*")
                }
            }
        }

        withType<ShadowJar> {
            if(System.getProperty("relocate") != null)//run with "-Drelocate" (jvm arg) to relocate for release, but don't do it for debug since it breaks hot-swap and takes longer
                dependsOn(getByName("relocate"))
            archiveFileName.set(project.name + "-all.jar")
            minimize()
            val shared = project.extensions.getByType(SharedProjectData::class.java)
            if(shared.main_cls != null)
                manifest {
                    attributes(mapOf("Main-Class" to shared.main_cls))
                }
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

		withType<ProcessResources> {
			filesMatching("plugin.yml") {
				expand(mapOf("version" to project.version, "name" to project.name, "main" to project.property("main_cls")))
			}
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