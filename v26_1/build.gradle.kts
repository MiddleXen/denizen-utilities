plugins {
    `java-library`
}

dependencies {
    implementation(project(":core"))
    compileOnly("org.spigotmc:spigot:26.1.2-R0.1-SNAPSHOT")
}