plugins {
    kotlin("jvm") version "1.6.20-RC"
}

group = "blocks.impl"
sharedProjectData.main_cls = "$group.BlocksPlugin"

setupShadowJar()
copyToPluginsFolder()

dependencies {
    compileOnly(Dependencies.LOMBOK)
    annotationProcessor(Dependencies.LOMBOK)
    compileOnly(Dependencies.PAPER)
    compileOnly(Dependencies._SPIGOT)
    compileOnly(Dependencies.LUCKO_HELPER)
    compileOnly(Dependencies.LUCKO_PROFILES)
    compileOnly(Dependencies.LUCKO_SQL)
    compileOnly(Dependencies.FASTUTIL)
    compileOnly(Dependencies.ACF_CORE)
    compileOnly(Dependencies.GUICE)
    compileOnly(Dependencies.ACF)
    compileOnly(Dependencies.LFC_SHARED)
    compileOnly(Dependencies.LFC_BUKKIT)
    compileOnly(Dependencies.ITEMS)
    compileOnly(Dependencies.MENUS)
    compileOnly(Dependencies.ORMLITE_CORE)
    compileOnly(Dependencies.ORMLITE_JDBC)
    compileOnly(project(":commons"))
    implementation(project(":blocks-api"))
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