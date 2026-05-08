plugins {
    java
}

group = "com.xphearts"
version = "1.3.4"
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
        // Fixed filename — no version suffix. The version lives in plugin.yml and
        // XPHearts.VERSION. A consistent filename means the server operator always
        // replaces one file and Paper never sees two JARs with the same plugin name.
        archiveFileName.set("XPHearts.jar")
    }
}
