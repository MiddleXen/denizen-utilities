plugins {
    `java-library`
}

dependencies {
    implementation(project(":core"))
    compileOnly("org.spigotmc:spigot:1.21.11-R0.2-SNAPSHOT:remapped-mojang")
    compileOnly("com.denizenscript:denizen:1.3.2-SNAPSHOT")
    compileOnly(fileTree(projectDir.resolve("libs")) { include("*.jar") })
}