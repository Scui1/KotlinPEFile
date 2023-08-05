plugins {
    kotlin("jvm") version "1.9.0"
    `java-library`
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("ch.qos.logback:logback-classic:1.4.9")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}