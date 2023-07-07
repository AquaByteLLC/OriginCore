group = "commons"
sharedProjectData.main_cls = "$group.CommonsPlugin"

setupShadowJar(mini = false)
copyToPluginsFolder()
setupKotlin()

dependencies {
    compileOnly(Dependencies.LOMBOK)
    annotationProcessor(Dependencies.LOMBOK)
    compileOnly(Dependencies.PAPER)
    compileOnly(Dependencies.SPIGOT)
    implementation(Dependencies.LUCKO_HELPER)
    implementation(Dependencies.LUCKO_SQL)
    implementation(Dependencies.LUCKO_REDIS)
    implementation(Dependencies.LUCKO_PROFILES)
    implementation(Dependencies.GUICE)
    implementation(Dependencies.ORMLITE_CORE)
    implementation(Dependencies.ORMLITE_JDBC)
    implementation(Dependencies.ACF_CORE)
    implementation(Dependencies.ACF)
    implementation(Dependencies.LFC_SHARED)
    implementation(Dependencies.LFC_BUKKIT)
    implementation(Dependencies.ITEMS)
    implementation(Dependencies.MENUS)
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.8.22") // kotlin is shaded in commons only, and other modules use the gradle plugin, automatically adding the dependency
}