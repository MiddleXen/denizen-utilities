plugins {
    `java-library`
}

dependencies {
    implementation(project(":core"))
    compileOnly("org.spigotmc:spigot:26.1.2-R0.1-SNAPSHOT")
    compileOnly("com.denizenscript:denizen:1.3.2-SNAPSHOT")
    compileOnly(fileTree(projectDir.resolve("libs")) { include("*.jar") })
}