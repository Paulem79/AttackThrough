import ovh.paulem.buildscript.NewGithubChangelog

plugins {
    id("java")

    id("com.modrinth.minotaur") version "2.8.10"
}

group = "io.github.paulem.attackthrough"
version = "1.0.2"

repositories {
    maven {
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")

        content {
            includeGroup("org.bukkit")
            includeGroup("org.spigotmc")
        }
    }

    maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots") }
    maven { url = uri("https://oss.sonatype.org/content/repositories/central") }
    mavenLocal()
    mavenCentral()
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.16.5-R0.1-SNAPSHOT") // Min 1.15.2
    compileOnly("com.google.guava:guava:33.5.0-jre")
    compileOnly("org.jetbrains:annotations:26.0.2-1")
}

modrinth {
    token.set(System.getenv("MODRINTH_TOKEN"))
    projectId.set("rKCOYOil")
    versionNumber.set(project.version.toString())
    versionName.set("Attack Through ${project.version}")
    versionType.set("release")
    changelog.set(NewGithubChangelog.getChangelog())
    uploadFile.set(tasks.jar)
    gameVersions.addAll(listOf("1.21.8", "1.21.7", "1.21.6", "1.21.5", "1.21.4", "1.21.3", "1.21.2", "1.21.1", "1.21", "1.20.6", "1.20.5", "1.20.4", "1.20.3", "1.20.2", "1.20.1", "1.20", "1.19.4", "1.19.3", "1.19.2", "1.19.1", "1.19", "1.18.2", "1.18.1", "1.18", "1.17.1", "1.17", "1.16.5", "1.16.4", "1.16.3", "1.16.2", "1.16.1", "1.16", "1.15.2"))
    loaders.addAll(listOf("bukkit", "folia", "paper", "purpur", "spigot"))
}

// ------------------------ MISC ------------------------
tasks.register<Task>("changelog") {
    doLast {
        val changelog = NewGithubChangelog.getChangelog()
        println(changelog)
    }
}

tasks.build {
    mustRunAfter(tasks.clean)
    dependsOn(tasks.clean)
}

java {
    withSourcesJar()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}

tasks.withType<JavaCompile> {
    sourceCompatibility = JavaVersion.VERSION_1_8.toString()
    targetCompatibility = JavaVersion.VERSION_1_8.toString()

    options.encoding = "UTF-8"
}