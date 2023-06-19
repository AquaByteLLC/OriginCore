setupShadowJar()

dependencies {
    compileOnly(Dependencies.LOMBOK)
    annotationProcessor(Dependencies.LOMBOK)
    compileOnly(Dependencies.SPIGOT)
    compileOnly(Dependencies.LUCKO_HELPER)
    compileOnly(Dependencies.LUCKO_SQL)
    implementation(Dependencies.REFLECTIONS)
    compileOnly(Dependencies.GUICE)
    compileOnly(Dependencies.APACHE_COMMONS)
    compileOnly(project(":commons"))
}