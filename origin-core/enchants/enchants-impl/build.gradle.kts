group = "enchants.impl"
sharedProjectData.main_cls = "$group.EnchantPlugin"

setupShadowJar()
copyToPluginsFolder()

dependencies {
    compileOnly(Dependencies.LOMBOK)
    annotationProcessor(Dependencies.LOMBOK)
    compileOnly(Dependencies.SPIGOT)
    compileOnly(Dependencies._SPIGOT)
    compileOnly(Dependencies.LUCKO_HELPER)
    compileOnly(Dependencies.LUCKO_SQL)
    compileOnly(Dependencies.GUICE)
    compileOnly(project(":commons"))
    implementation(project(":enchants-api"))
}