plugins {
    kotlin("jvm") version "1.6.20-RC"
}

group = "generators.impl"
sharedProjectData.main_cls = "$group.GensPlugin"

setupShadowJar()
copyToPluginsFolder()

dependencies {
    compileOnly(Dependencies.LOMBOK)
    annotationProcessor(Dependencies.LOMBOK)
    compileOnly(Dependencies.SPIGOT)
    compileOnly(Dependencies.LUCKO_HELPER)
    compileOnly(Dependencies.LUCKO_PROFILES)
    compileOnly(Dependencies.LUCKO_SQL)
    compileOnly(Dependencies.FASTUTIL)
    implementation(Dependencies.ACF)
    implementation(Dependencies.LFC_SHARED)
    implementation(Dependencies.LFC_BUKKIT)
    implementation(Dependencies.ITEMS)
    implementation(Dependencies.MENUS)
    compileOnly(Dependencies.ORMLITE)
    implementation(project(":commons"))
    implementation(project(":generators-api"))
}