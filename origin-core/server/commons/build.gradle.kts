group = "commons"
sharedProjectData.main_cls = "$group.CommonsPlugin"

setupShadowJar(mini = false)
copyToPluginsFolder()

dependencies {
    compileOnly(Dependencies.LOMBOK)
    annotationProcessor(Dependencies.LOMBOK)
    compileOnly(Dependencies.PAPER)
    compileOnly(Dependencies._SPIGOT)
    implementation(Dependencies.LUCKO_HELPER)
    implementation(Dependencies.LUCKO_SQL)
    implementation(Dependencies.LUCKO_PROFILES)
    implementation(Dependencies.GUICE)
    implementation(Dependencies.ORMLITE)
    implementation(Dependencies.ACF)
    implementation(Dependencies.LFC_SHARED)
    implementation(Dependencies.LFC_BUKKIT)
    implementation(Dependencies.ITEMS)
    implementation(Dependencies.MENUS)
}