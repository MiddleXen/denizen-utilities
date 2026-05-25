plugins {
    `java-library`
}

dependencies {
    implementation(project(":core"))
    compileOnly("io.papermc.paper:paper-api:26.1.2.build.9-alpha")
    compileOnly("com.denizenscript:denizen:1.3.2-SNAPSHOT")
    compileOnly(fileTree(projectDir.resolve("libs")) { include("*.jar") })
}