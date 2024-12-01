import com.github.jengelman.gradle.plugins.shadow.transformers.Log4j2PluginsCacheFileTransformer

plugins {
    java
    application
    id("com.gradleup.shadow") version "8.3.2"
}

group = "dev.metabrix.urfu"
version = "1.1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains:annotations:25.0.0")

    implementation("com.typesafe:config:1.4.3")
    implementation("org.json:json:20240303")

    implementation("org.slf4j:slf4j-api:2.0.16")
    implementation("org.apache.logging.log4j:log4j-core:2.24.0")
    implementation("org.apache.logging.log4j:log4j-slf4j2-impl:2.24.0")
    implementation("net.minecrell:terminalconsoleappender:1.3.0")

    implementation("org.telegram:telegrambots:6.9.7.1")

    implementation("com.mysql:mysql-connector-j:9.1.0")

    testImplementation(platform("org.junit:junit-bom:5.11.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

val targetJavaVersion = 21
java {
    val javaVersion = JavaVersion.toVersion(targetJavaVersion)
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
    if (JavaVersion.current() < javaVersion) {
        toolchain.languageVersion.set(JavaLanguageVersion.of(targetJavaVersion))
    }
}

tasks.shadowJar {
    mergeServiceFiles()
    transform(Log4j2PluginsCacheFileTransformer::class.java)
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass = "dev.metabrix.urfu.oopbot.Main"
}
