group = "blocks.impl"
sharedProjectData.main_cls = "$group.BlocksPlugin"

setupShadowJar()
copyToPluginsFolder("commons")

dependencies {
    compileOnly(Dependencies.LOMBOK)
    annotationProcessor(Dependencies.LOMBOK)
    compileOnly(Dependencies.PAPER)
    compileOnly(Dependencies.SPIGOT)
    compileOnly(Dependencies.LUCKO_HELPER)
    compileOnly(Dependencies.LUCKO_PROFILES)
    compileOnly(Dependencies.LUCKO_SQL)
    compileOnly(Dependencies.FASTUTIL)
    compileOnly(Dependencies.ACF_CORE)
    compileOnly(Dependencies.GUICE)
    compileOnly(Dependencies.ACF)
    compileOnly(Dependencies.LFC_SHARED)
    compileOnly(Dependencies.LFC_BUKKIT)
    compileOnly(Dependencies.ITEMS)
    compileOnly(Dependencies.MENUS)
    compileOnly(Dependencies.ORMLITE_CORE)
    compileOnly(Dependencies.ORMLITE_JDBC)
    compileOnly(Dependencies.WORLDEDIT) // For the /protect command
    compileOnly(project(":commons"))
    implementation(project(":blocks-api"))
}