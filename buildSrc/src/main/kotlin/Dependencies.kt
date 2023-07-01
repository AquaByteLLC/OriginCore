// Contains all version information for out dependencies.
object Versions {
    const val MINECRFAFT_VERSION = "1.19.4-R0.1-SNAPSHOT"
    const val LOMBOK_VERSION = "1.18.20"
    const val REFLECTIONS_VERSION = "0.10.2";
    const val WORLDGUARDWRAPPER_VERSION = "1.2.0-SNAPSHOT"
    const val LUCKO_HELPER_VERSION = "5.6.10"
    const val LUCKO_SQL_VERSION = "1.3.0"
    const val LUCKO_MONGO_VERSION = "1.2.0"
    const val LUCKO_REDIS_VERSION = "1.2.0"
    const val LUCKO_LILLY_PAD_VERSION = "2.2.0"
    const val LUCKO_PROFILES_VERSION = "1.2.0"
    const val ACF_VERSION = "0.5.1-SNAPSHOT"
    const val GUICE_VERSION = "5.1.0"
    const val LFC_VERSION = "1.5"
    const val MENUS_VERSION = "1.0.0"
    const val ITEMS_VERSION = "1.0"
    const val FASTUTIL_VERSION = "8.5.6"
    const val ORMLITE_VERSION = "6.1"
    const val WORLD_GUARD_VERSION = "7.0.8"
}

object Dependencies {
    const val SPIGOT = "org.spigotmc:spigot:${Versions.MINECRFAFT_VERSION}"
    const val PAPER = "io.papermc.paper:paper-api:${Versions.MINECRFAFT_VERSION}"
    const val LOMBOK = "org.projectlombok:lombok:${Versions.LOMBOK_VERSION}"
    const val REFLECTIONS = "org.reflections:reflections:${Versions.REFLECTIONS_VERSION}"
    const val WORLDGUARDWRAPPER = "org.codemc.worldguardwrapper:worldguardwrapper:${Versions.WORLDGUARDWRAPPER_VERSION}"
    const val WORLD_GUARD = "com.sk89q.worldguard:worldguard-bukkit:${Versions.WORLD_GUARD_VERSION}"
    const val LUCKO_HELPER = "me.lucko:helper:${Versions.LUCKO_HELPER_VERSION}"
    const val LUCKO_SQL = "me.lucko:helper-sql:${Versions.LUCKO_SQL_VERSION}"
    const val LUCKO_REDIS = "me.lucko:helper-redis:${Versions.LUCKO_REDIS_VERSION}"
    const val LUCKO_MONGO = "me.lucko:helper-mongo:${Versions.LUCKO_MONGO_VERSION}"
    const val LUCKO_LILLYPAD = "me.lucko:helper-lilypad:${Versions.LUCKO_LILLY_PAD_VERSION}"
    const val LUCKO_PROFILES = "me.lucko:helper-profiles:${Versions.LUCKO_PROFILES_VERSION}"
    const val ACF_CORE = "co.aikar:acf-core:${Versions.ACF_VERSION}"
    const val ACF = "co.aikar:acf-paper:${Versions.ACF_VERSION}"
    const val GUICE = "com.google.inject:guice:${Versions.GUICE_VERSION}"
    const val LFC_SHARED = "me.vadim.util.conf:LiteConfig-shared:${Versions.LFC_VERSION}"
    const val LFC_BUKKIT = "me.vadim.util.conf:LiteConfig-bukkit:${Versions.LFC_VERSION}"
    const val ITEMS = "me.vadim.util.item:Items:${Versions.ITEMS_VERSION}"
    const val MENUS = "me.vadim.util.menus:Menus:${Versions.MENUS_VERSION}"
    const val FASTUTIL = "it.unimi.dsi:fastutil:${Versions.FASTUTIL_VERSION}"
    const val ORMLITE_CORE = "com.j256.ormlite:ormlite-core:${Versions.ORMLITE_VERSION}"
    const val ORMLITE_JDBC = "com.j256.ormlite:ormlite-jdbc:${Versions.ORMLITE_VERSION}"
}