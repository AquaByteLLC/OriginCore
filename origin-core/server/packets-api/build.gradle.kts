setupShadowJar()

dependencies {
    compileOnly(Dependencies.LOMBOK)
    annotationProcessor(Dependencies.LOMBOK)
    compileOnly(Dependencies.PAPER)
    compileOnly(Dependencies.LUCKO_HELPER)
    compileOnly(Dependencies.LUCKO_SQL)
    compileOnly(Dependencies._SPIGOT)
    compileOnly(Dependencies.GUICE)
    implementation(project(":commons"))
}