plugins {
    id("java-library")
    id("maven-publish")
}

group = "${project.property("group")}"
version = "${project.property("version")}"

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://repo.codemc.io/repository/maven-public/")
}

dependencies {
    compileOnly(group = "org.spigotmc", name = "spigot-api", version = "1.20.4-R0.1-SNAPSHOT")
    compileOnly(group = "me.clip", name = "placeholderapi", version = "2.11.5")
    compileOnly(group = "org.popcraft", name = "chunky-common", version = "${project.property("target")}")
    compileOnly(group = "org.popcraft", name = "chunky-bukkit", version = "${project.property("target")}")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
    processResources {
        filesMatching("plugin.yml") {
            expand(
                "name" to rootProject.name,
                "version" to project.version,
                "group" to project.group,
                "author" to project.property("author"),
                "description" to project.property("description"),
            )
        }
    }
}

publishing {
    repositories {
        if (project.hasProperty("mavenUsername") && project.hasProperty("mavenPassword")) {
            maven {
                credentials {
                    username = "${project.property("mavenUsername")}"
                    password = "${project.property("mavenPassword")}"
                }
                url = uri("https://repo.codemc.io/repository/maven-releases/")
            }
        }
    }
    publications {
        create<MavenPublication>("maven") {
            groupId = "${project.group}"
            artifactId = project.name
            version = "${project.version}"
            from(components["java"])
        }
    }
}
