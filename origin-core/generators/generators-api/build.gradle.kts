setupShadowJar()

dependencies {
    compileOnly(Dependencies.LOMBOK)
    annotationProcessor(Dependencies.LOMBOK)
    compileOnly(Dependencies.PAPER)
    compileOnly(project(":blocks-api"))
    compileOnly(project(":commons"))
}