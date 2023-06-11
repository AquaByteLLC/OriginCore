rootProject.name = "OriginCore"

setupOriginModule(
    "origin-core",
        listOf(
            Pair("enchants", listOf("enchants-api", "enchants-impl")),
            Pair("server", listOf("packets-api", "commons"))
        ));


fun setupOriginModule(base: String, setup: List<Pair<String, List<String>>>) =
    setup.forEach { pair
        -> pair.second.forEach { name
            -> setupSubproject(name, file("$base/${pair.first}/$name"))
        }
    }


fun setupSubproject(name: String, projectDirectory: File) = setupSubproject(name) {
    projectDir = projectDirectory
}

inline fun setupSubproject(name: String, block: ProjectDescriptor.() -> Unit) {
    include(name)
    project(":$name").apply(block)
}