plugins {
    `java-gradle-plugin`
    kotlin("jvm") version "2.0.0"
}

gradlePlugin {
    plugins {
        create("jsonProjectProperties") {
            id = "ru.yarsu.json-project-properties"
            implementationClass = "ru.yarsu.JsonProjectPropertiesPlugin"

        }
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.17.2")
}


