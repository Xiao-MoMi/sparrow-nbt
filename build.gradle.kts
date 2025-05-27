plugins {
    id("java")
    id("maven-publish")
}

group = "net.momirealms"
version = "0.8"

repositories {
    mavenCentral()
    maven("https://libraries.minecraft.net/")
}

dependencies {
    compileOnly("org.jetbrains:annotations:24.0.0")
    compileOnly("net.kyori:adventure-api:4.21.0")
    compileOnly("net.kyori:option:1.1.0")
    compileOnly("com.mojang:datafixerupper:6.0.8")
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
    withSourcesJar()
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.release.set(21)
    dependsOn(tasks.clean)
}

publishing {
    repositories {
        maven {
            url = uri("https://repo.momirealms.net/releases")
            credentials(PasswordCredentials::class) {
                username = System.getenv("REPO_USERNAME")
                password = System.getenv("REPO_PASSWORD")
            }
        }
    }
    publications {
        create<MavenPublication>("mavenJava") {
            groupId = "net.momirealms"
            artifactId = "sparrow-nbt"
            from(components["java"])
            pom {
                name = "Sparrow NBT"
                url = "https://github.com/Xiao-MoMi/sparrow-nbt"
                licenses {
                    license {
                        name = "MIT"
                        url = "https://opensource.org/licenses/MIT"
                        distribution = "repo"
                    }
                }
            }
        }
    }
}

