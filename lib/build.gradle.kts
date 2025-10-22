plugins {
    kotlin("jvm") version "2.2.20"

    id("maven-publish")
    `java-library`
}

group = "de.scui"
version = "1.4.0"

repositories {
    mavenCentral()
}

dependencies {
    api("io.github.icedland.iced:iced-x86:1.21.0")
    implementation("ch.qos.logback:logback-classic:1.5.19")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
    withSourcesJar()
}

publishing {

    repositories {
        maven {
            group
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/Scui1/KotlinPEFile")
            credentials {
                username = System.getenv("GITHUB_USERNAME")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }

    publications {
        register<MavenPublication>("gpr") {
            artifactId = "kotlin-pefile"

            from(components["java"])
        }
    }
}