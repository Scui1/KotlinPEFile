plugins {
    kotlin("jvm") version "1.9.0"

    id("maven-publish")
    `java-library`
}

group = "de.scui"
version = "1.3.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("ch.qos.logback:logback-classic:1.4.9")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
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