plugins {
    `java-library`
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.21"
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:26.1.2.build.9-alpha")

    compileOnly("net.skinsrestorer:skinsrestorer-api:15.11.0")
    compileOnly("io.github.toxicity188:bettermodel-bukkit-api:3.0.1")
    compileOnly("com.discordsrv:discordsrv:1.28.0")
    paperweight.paperDevBundle("26.1.2.build.+")
}

tasks.processResources {
    filesMatching("plugin.yml") {
        expand(mapOf("version" to project.version))
    }
}