setupShadowJar()

dependencies {
    compileOnly(Dependencies.LOMBOK)
    annotationProcessor(Dependencies.LOMBOK)
    compileOnly(Dependencies.PAPER)
    compileOnly(Dependencies.ACF)
    compileOnly(project(":commons"))
}