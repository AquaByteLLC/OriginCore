group = "commons"
sharedProjectData.main_cls = "$group.CommonsPlugin"

setupShadowJar()
copyToPluginsFolder()

dependencies {
    compileOnly(Dependencies.LOMBOK)
    annotationProcessor(Dependencies.LOMBOK)
    compileOnly(Dependencies.SPIGOT)
    compileOnly(Dependencies.LUCKO_HELPER)
    compileOnly(Dependencies.LUCKO_SQL)
    compileOnly(Dependencies._SPIGOT)
    compileOnly(Dependencies.GUICE)
    compileOnly(Dependencies.ORMLITE)
}