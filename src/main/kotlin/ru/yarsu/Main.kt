package ru.yarsu

import com.beust.jcommander.IStringConverter
import com.beust.jcommander.JCommander
import com.beust.jcommander.Parameter
import com.beust.jcommander.Parameters
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import org.http4k.server.Netty
import org.http4k.server.asServer
import ru.yarsu.classes.category.Category
import ru.yarsu.classes.category.CategoryStorage
import ru.yarsu.classes.data.Color
import ru.yarsu.classes.data.Importance
import ru.yarsu.classes.data.MAX_PORT
import ru.yarsu.classes.data.MIN_PORT
import ru.yarsu.classes.task.Task
import ru.yarsu.classes.task.TasksStorage
import ru.yarsu.classes.user.User
import ru.yarsu.classes.user.UsersStorage
import web.routes.routes
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeParseException
import java.util.UUID
import kotlin.concurrent.thread
import kotlin.system.exitProcess

class UUIDConverter : IStringConverter<UUID> {
    override fun convert(value: String): UUID = UUID.fromString(value)
}

@Parameters(separators = "=", commandDescription = "Команда по умолчанию")
class Default {
    @Parameter(names = ["--tasks-file"], required = true)
    var tasksFile: String? = null

    @Parameter(names = ["--categories-file"], required = true)
    var categoryFile: String? = null

    @Parameter(names = ["--users-file"], required = true)
    var usersFile: String? = null

    @Parameter(names = ["--port"], required = true)
    var port: Int? = null
}

fun main(args: Array<String>) {
    val default: Default = Default()

    val jCommander =
        JCommander
            .newBuilder()
            .addObject(default)
            .build()

    try {
        jCommander.parse(*args)
        val tasksPath = default.tasksFile ?: exitProcess(1)
        val categoryPath = default.categoryFile ?: exitProcess(1)
        val usersPath = default.usersFile ?: exitProcess(1)
        val portPath = default.port ?: exitProcess(1)
        if (default.port !in MIN_PORT..MAX_PORT) {
            exitProcess(1)
        }

        val tasksList = readTasks(tasksPath)
        val tasksStorage = TasksStorage(tasksList)

        val categoryList = readCategory(categoryPath)
        val categoryStorage = CategoryStorage(categoryList)

        val usersList = readUsers(usersPath)
        val usersStorage = UsersStorage(usersList)

        val app = routes(tasksStorage, categoryStorage, usersStorage)
        val server = app.asServer(Netty(portPath))

        Runtime.getRuntime().addShutdownHook(
            thread(start = false) {
                val taskCsvData = tasksStorage.toCsv()
                File(tasksPath).writeText(taskCsvData)
                val categoryCsvData = categoryStorage.toCsv()
                File(categoryPath).writeText(categoryCsvData)
                val userCsvData = usersStorage.toCsv()
                File(usersPath).writeText(userCsvData)
            },
        )
        val serverThread =
            thread(start = true) {
                server.start()
            }
        println("Press Enter to stop the application...")
        readlnOrNull()
        println("Stopping application...")
        try {
            server.stop()
            val taskCsvData = tasksStorage.toCsv()
            File(tasksPath).writeText(taskCsvData)
            val categoryCsvData = categoryStorage.toCsv()
            File(categoryPath).writeText(categoryCsvData)
            val userCsvData = usersStorage.toCsv()
            File(usersPath).writeText(userCsvData)
            println("Data saved successfully.")
        } catch (e: Exception) {
            println("Error during shutdown: ${e.message}")
        }
        println("Application stopped.")
    } catch (e: Exception) {
        println(e.message)
        exitProcess(1)
    }
}

fun readTasks(path: String): List<Task> {
    val tasksList: MutableList<Task> = mutableListOf()
    csvReader().open(path) {
        this.readAllWithHeaderAsSequence().forEach {
            val id = UUID.fromString(it["Id"])
            val title = it["Title"].toString()
            val registrationDateTime = LocalDateTime.parse(it["RegistrationDateTime"])
            val startDateTime = LocalDateTime.parse(it["StartDateTime"])
            val endDateTime =
                try {
                    LocalDateTime.parse(it["EndDateTime"])
                } catch (e: DateTimeParseException) {
                    null
                }
            val importance = Importance.fromType(it["Importance"].toString())
            val urgency = it["Urgency"].toBoolean()
            val percentage = it["Percentage"]!!.toInt()
            val description = it["Description"].toString()
            val author = UUID.fromString(it["Author"])
            val categoryId =
                try {
                    UUID.fromString(it["Category"])
                } catch (e: IllegalArgumentException) {
                    null
                }
            val task =
                Task(
                    id = id,
                    title = title,
                    registrationDateTime = registrationDateTime,
                    startDateTime = startDateTime,
                    endDateTime = endDateTime,
                    importance = importance!!,
                    urgency = urgency,
                    percentage = percentage,
                    description = description,
                    author = author,
                    categoryId = categoryId,
                )
            tasksList.add(task)
        }
    }
    return tasksList
}

fun readCategory(path: String): List<Category> {
    val categoryList: MutableList<Category> = mutableListOf()
    csvReader().open(path) {
        readAllWithHeaderAsSequence().forEach {
            val id = UUID.fromString(it["Id"])
            val description = it["Description"].toString()
            val color = Color.valueOf(it["Color"]!!)
            val owner =
                try {
                    UUID.fromString(it["Owner"])
                } catch (e: IllegalArgumentException) {
                    null
                }
            val category =
                Category(
                    id = id,
                    description = description,
                    color = color,
                    owner = owner,
                )
            categoryList.add(category)
        }
    }
    return categoryList
}

fun readUsers(path: String): List<User> {
    val usersList: MutableList<User> = mutableListOf()
    csvReader().open(path) {
        readAllWithHeaderAsSequence().forEach {
            val id = UUID.fromString(it["Id"])
            val login = it["Login"]!!
            val registrationDateTime = LocalDateTime.parse(it["RegistrationDateTime"])
            val email = it["Email"]!!
            val user = User(id, login, registrationDateTime, email)
            usersList.add(user)
        }
    }
    return usersList
}
