plugins {
    kotlin("jvm") version "1.6.20-RC"
}

group = "fishing.impl"
sharedProjectData.main_cls = "$group.FishingPlugin"

setupShadowJar()
copyToPluginsFolder()

dependencies {
    compileOnly(Dependencies.LOMBOK)
    compileOnly(Dependencies.ACF)
    annotationProcessor(Dependencies.LOMBOK)
    compileOnly(Dependencies.PAPER)
    compileOnly(Dependencies._SPIGOT)
    compileOnly(Dependencies.LUCKO_HELPER)
    compileOnly(Dependencies.LUCKO_SQL)
    compileOnly(Dependencies.GUICE)
    compileOnly(Dependencies.LFC_SHARED)
    compileOnly(Dependencies.LFC_BUKKIT)
    compileOnly(Dependencies.ITEMS)
    compileOnly(Dependencies.MENUS)
    compileOnly(project(":commons"))
    implementation(project(":fishing-api"))
}

tasks {
    compileKotlin {
        kotlinOptions {
            jvmTarget = "17"
        }
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }

    compileJava {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }
}