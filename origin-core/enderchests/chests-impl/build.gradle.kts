group = "enderchests.impl"
sharedProjectData.main_cls = "$group.EnderChestsPlugin"

setupKotlin()
setupShadowJar()
copyToPluginsFolder("commons", "settings-impl", "blocks-impl")

dependencies {
    compileOnly(Dependencies.LOMBOK)
    compileOnly(Dependencies.ACF)
    annotationProcessor(Dependencies.LOMBOK)
    compileOnly(Dependencies.PAPER)
    compileOnly(Dependencies.SPIGOT)
    compileOnly(Dependencies.LUCKO_HELPER)
    compileOnly(Dependencies.LUCKO_SQL)
    compileOnly(Dependencies.LFC_SHARED)
    compileOnly(Dependencies.LFC_BUKKIT)
    compileOnly(Dependencies.ITEMS)
    compileOnly(Dependencies.MENUS)
    compileOnly(Dependencies.ORMLITE_CORE)
    compileOnly(Dependencies.ORMLITE_JDBC)
    compileOnly(project(":commons"))
    compileOnly(project(":settings-api"))
    implementation(project(":chests-api"))
    compileOnly(project(":blocks-api"))
    compileOnly(project(":blocks-impl"))
}