allprojects {
    extensions.create("sharedProjectData", SharedProjectData::class.java)
}

plugins {
    id("java")
}

subprojects {

    apply {
        plugin<JavaPlugin>()
    }

    // Here we only want to define the needed repositories for the subprojects.
    repositories {
        mavenLocal()
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        maven("https://repo.aikar.co/content/groups/aikar/")
        maven("https://repo.codemc.org/repository/maven-public/")
        maven("https://oss.sonatype.org/content/repositories/snapshots")
        maven("https://oss.sonatype.org/content/repositories/central")
        maven("https://repo.dmulloy2.net/repository/public/")
    }
}