setupShadowJar()

dependencies {
    compileOnly(Dependencies.LOMBOK)
    annotationProcessor(Dependencies.LOMBOK)
    compileOnly(Dependencies.PAPER)
    compileOnly(Dependencies.SPIGOT)
    compileOnly(Dependencies.LUCKO_HELPER)
    compileOnly(Dependencies.LUCKO_SQL)
    implementation(Dependencies.REFLECTIONS)
    compileOnly(Dependencies.LFC_SHARED)
    compileOnly(Dependencies.LFC_BUKKIT)
    compileOnly(Dependencies.GUICE)
    compileOnly(Dependencies.WORLDEDIT)
    compileOnly(Dependencies.WORLDGUARD)
    compileOnly(project(":commons"))
}