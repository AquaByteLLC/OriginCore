setupShadowJar()

plugins {
    id("java")
}

subprojects {

    apply {
        plugin<JavaPlugin>()
    }

    // Here we only want to define the needed repositories for the subprojects.
    repositories {
        mavenCentral()
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        maven("https://repo.codemc.org/repository/maven-public/")
        /*
         As Spigot-API depends on the Bungeecord ChatComponent-API,
        we need to add the Sonatype OSS repository, as Gradle,
        in comparison to maven, doesn't want to understand the ~/.m2
        directory unless added using mavenLocal(). Maven usually just gets
        it from there, as most people have run the BuildTools at least once.
        This is therefore not needed if you're using the full Spigot/CraftBukkit,
        or if you're using the Bukkit API.
        */
        maven("https://oss.sonatype.org/content/repositories/snapshots")
        maven("https://oss.sonatype.org/content/repositories/central")
    }
}
