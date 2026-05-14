plugins {
    java
}

group = "com.xphearts"
version = "1.3.6"
description = "XPHearts - Extra hearts, grindstone XP bottling, XP multiplier charms, and rotten flesh smelting"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.11.6")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
        options.release.set(21)
    }
    jar {
        // Each version gets its own release folder: build/releases/VERSION/xphearts-VERSION.jar
        // This keeps builds organized and prevents old versioned JARs from accumulating
        // in build/libs alongside new ones.
        archiveFileName.set("xphearts-${version}.jar")
        destinationDirectory.set(layout.buildDirectory.dir("releases/${version}"))
    }
}
