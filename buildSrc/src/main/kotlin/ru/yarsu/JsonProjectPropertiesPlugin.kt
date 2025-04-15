package ru.yarsu

import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaApplication
import java.io.File

data class BuildProperties(
    val mainClass: String = "",
    val dependencies: List<String> = emptyList(),
)

class JsonProjectPropertiesPlugin : Plugin<Project> {
    private val jsonDependencies = "build.properties.json"

    override fun apply(project: Project) {
        val jsonFile = getAndCheckConfigurationFile(project) ?: return
        val properties = readConfigFile(jsonFile)
        addImplementationDependencies(project, properties.dependencies)
        applyAndConfigureApplication(project, properties.mainClass)
    }

    private fun getAndCheckConfigurationFile(project: Project): File? {
        val jsonFile: File = project.file(jsonDependencies)
        if (!jsonFile.exists()) {
            println("Конфигурационный файл сборки проекта $jsonFile не был найден")
            return null
        }
        return jsonFile
    }

    private fun readConfigFile(file: File): BuildProperties {
        val mapper = jacksonObjectMapper()
        try {
            val buildProperties: BuildProperties = mapper.readValue(file)
            return buildProperties
        } catch (mapperException: JsonMappingException) {
            println("Не удалось считать конфигурационный файл $file")
            println(mapperException.message)
            return BuildProperties()
        }
    }

    private fun addImplementationDependencies(project: Project, dependencies: List<String>) {
        for(dependency in dependencies) {
            project.dependencies.add("implementation", dependency)
        }
    }

    private fun applyAndConfigureApplication(project: Project, mainClass: String) {
        if (!project.plugins.hasPlugin("application")) {
            project.pluginManager.apply("application")
        }
        val found = project.extensions.getByType(JavaApplication::class.java)
        found.mainClass.set(mainClass)
    }
}
