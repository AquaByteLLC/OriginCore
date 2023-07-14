@echo off
echo:
set mvn=apache-maven-3.9.3\bin\mvn.cmd
echo Using local maven: %mvn%
call "%mvn%" --version
echo:
call %mvn% install:install-file -Dfile=LiteConfig-bukkit-1.6.jar -DgroupId=me.vadim.util.conf -DartifactId=LiteConfig-bukkit -Dversion=1.6 -Dpackaging=jar
call %mvn% install:install-file -Dfile=LiteConfig-shared-1.6.jar -DgroupId=me.vadim.util.conf -DartifactId=LiteConfig-shared -Dversion=1.6 -Dpackaging=jar
call %mvn% install:install-file -Dfile=LiteConfig-gson-1.6.jar -DgroupId=me.vadim.util.conf -DartifactId=LiteConfig-gson -Dversion=1.6 -Dpackaging=jar
call %mvn% install:install-file -Dfile=Items-1.0.jar -DgroupId=me.vadim.util.item -DartifactId=Items -Dversion=1.0 -Dpackaging=jar
call %mvn% install:install-file -Dfile=Menus-1.0.0.jar -DgroupId=me.vadim.util.menus -DartifactId=Menus -Dversion=1.0.0 -Dpackaging=jar
PAUSE