group = "mining.impl"
sharedProjectData.main_cls = "$group.MiningPlugin"

setupShadowJar()
copyToPluginsFolder()

dependencies {
    compileOnly(Dependencies.LOMBOK)
    compileOnly(Dependencies.ACF)
    annotationProcessor(Dependencies.LOMBOK)
    compileOnly(Dependencies.PAPER)
    compileOnly(Dependencies.SPIGOT)
    compileOnly(Dependencies.LUCKO_HELPER)
    compileOnly(Dependencies.LUCKO_SQL)
    compileOnly(Dependencies.GUICE)
    compileOnly(Dependencies.LFC_SHARED)
    compileOnly(Dependencies.LFC_BUKKIT)
    compileOnly(Dependencies.ITEMS)
    compileOnly(Dependencies.MENUS)
    compileOnly(Dependencies.WORLD_GUARD)
    compileOnly(project(":commons"))
    compileOnly(project(":enchants-api"))
    compileOnly(project(":blocks-api"))
    compileOnly(project(":blocks-impl"))
    implementation(project(":mining-api"))
}