plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.17.4"
}

group = "com.jakubbone"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

intellij {
    version.set("2024.1.7")
    type.set("IC") // IntelliJ IDEA Community Edition
    plugins.set(listOf("java"))
}

tasks {
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }

    patchPluginXml {
        sinceBuild.set("241")
        untilBuild.set("243.*")
    }
}