rootProject.name = "OriginCore"

setupOriginModule(
    "origin-core",
        listOf(
            Pair("mining", listOf("mining-api", "mining-impl")),
            Pair("farming", listOf("farming-api", "farming-impl")),
            Pair("fishing", listOf("fishing-api", "fishing-impl")),
            Pair("blocks", listOf("blocks-api")),
            Pair("enchants", listOf("enchants-api", "enchants-impl")),
            Pair("generators", listOf("generators-api", "generators-impl")),
            Pair("enderchests", listOf("chests-api", "chests-impl")),
            Pair("server", listOf("commons"))
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