plugins {
    kotlin("jvm") version "2.0.20"
    id("ru.yarsu.json-project-properties")
    id("org.jlleitschuh.gradle.ktlint") version "12.1.1"
    id("io.gitlab.arturbosch.detekt") version "1.23.7"
}

group = "ru.ac.uniyar"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.http4k:http4k-core:5.32.0.0")
    implementation("org.http4k:http4k-client-apache:5.32.0.0")
    implementation("com.fasterxml.jackson.core:jackson-core:2.17.2")
    implementation("com.jsoizo:kotlin-csv:1.10.0")
    implementation("org.jcommander:jcommander:2.0")
    implementation("org.slf4j:slf4j-simple:1.7.36")
    implementation("org.http4k:http4k-server-netty:5.32.0.0")
    implementation("org.http4k:http4k-format-jackson:5.32.0.0")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

ktlint {
    version.set("1.3.1")
}
tasks.withType<JavaExec> {
    standardInput = System.`in`
    systemProperty("file.encoding", "UTF-8")
    systemProperty("sun.stdout.encoding", "UTF-8")
    systemProperty("sun.stderr.encoding", "UTF-8")
}

val ktlintVersion: String by project

ktlint {
    version.set(ktlintVersion)
}
detekt {
    allRules = true
    buildUponDefaultConfig = true
}
