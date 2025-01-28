plugins {
    id("java")
}

group = "io.github.paulem.attackthrough"
version = "1.0.1"

repositories {
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://oss.sonatype.org/content/repositories/central")
    mavenLocal()
    mavenCentral()
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.15.2-R0.1-SNAPSHOT")
    compileOnly("com.google.guava:guava:33.4.0-jre")
    compileOnly("org.jetbrains:annotations:24.1.0")
}

tasks.compileJava {
    options.encoding = "UTF-8"
    JavaVersion.VERSION_1_8.toString().also {
        sourceCompatibility = it
        targetCompatibility = it
    }
}