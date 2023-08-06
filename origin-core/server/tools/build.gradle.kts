group = "tools.impl"
sharedProjectData.main_cls = "$group.ToolsPlugin"

setupShadowJar()
copyToPluginsFolder("commons")
setupKotlin()
setupJUnit()

dependencies {
	compileOnly(Dependencies.LOMBOK)
	compileOnly(Dependencies.ACF_CORE)
	compileOnly(Dependencies.ACF)
	annotationProcessor(Dependencies.LOMBOK)
	compileOnly(Dependencies.PAPER)
	compileOnly(Dependencies.SPIGOT)
	compileOnly(Dependencies.LUCKO_HELPER)
	compileOnly(Dependencies.LUCKO_SQL)
	compileOnly(Dependencies.GUICE)
	compileOnly(Dependencies.LFC_SHARED)
	compileOnly(Dependencies.LFC_BUKKIT)
	compileOnly(Dependencies.ITEMS)
	compileOnly(Dependencies.MENUS)
	compileOnly(Dependencies.WORLDGUARD)
	compileOnly(Dependencies.ORMLITE_CORE)
	compileOnly(Dependencies.ORMLITE_JDBC)
	compileOnly(Dependencies.SHELFTOR)
	compileOnly(project(":commons"))
	implementation(project(":settings-api"))
	testImplementation(Dependencies.JUNIT)
}