plugins {
    id("java")
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.21"
}

group = "de.pascalpex"
version = "2.4"

repositories {
    mavenCentral()
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
        url = uri("https://repo.extendedclip.com/releases/")
    }
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:6.0.3"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    compileOnly("me.clip:placeholderapi:2.12.2")
    paperweight.paperDevBundle("26.1.2.build.+")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
}

tasks.assemble {
    dependsOn(tasks.jar)
}
tasks.test {
    useJUnitPlatform()
}

paperweight.reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.MOJANG_PRODUCTION