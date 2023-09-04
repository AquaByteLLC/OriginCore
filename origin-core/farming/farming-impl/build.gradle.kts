group = "farming.impl"
sharedProjectData.main_cls = "$group.FarmingPlugin"

setupShadowJar()
setupKotlin()
copyToPluginsFolder("commons", "settings-impl", "blocks-impl", "tools")

dependencies {
    compileOnly(Dependencies.LOMBOK)
    compileOnly(Dependencies.ACF)
    annotationProcessor(Dependencies.LOMBOK)
    compileOnly(Dependencies.PAPER)
    compileOnly(Dependencies.SPIGOT)
    compileOnly(Dependencies.LUCKO_HELPER)
    compileOnly(Dependencies.LUCKO_SQL)
    compileOnly(Dependencies.DECENT_HOLOGRAMS)
    compileOnly(Dependencies.GUICE)
    compileOnly(Dependencies.LFC_SHARED)
    compileOnly(Dependencies.LFC_BUKKIT)
    compileOnly(Dependencies.ITEMS)
    compileOnly(Dependencies.MENUS)
    compileOnly(Dependencies.WORLDGUARD)
    compileOnly(Dependencies.SHELFTOR)
    compileOnly(project(":commons"))
    compileOnly(project(":blocks-api"))
    compileOnly(project(":settings-api"))
    compileOnly(project(":blocks-impl"))
    compileOnly(project(":tools"))
}