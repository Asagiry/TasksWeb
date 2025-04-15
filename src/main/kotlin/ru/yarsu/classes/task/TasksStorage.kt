package ru.yarsu.classes.task

import java.util.UUID

class TasksStorage(
    private var list: List<Task>,
) {
    private val tasksList = list.associateBy { it.id }.toMutableMap()

    fun getList(): List<Task> = tasksList.values.toList()

    fun addTask(task: Task) {
        tasksList[task.id] = task
    }

    fun getTask(id: UUID): Task? = tasksList[id]

    fun removeTask(id: UUID) {
        tasksList.remove(id)
    }

    fun changeList(newList: List<Task>) {
        list = newList
    }

    fun toCsv(): String {
        val header =
            listOf(
                "Id",
                "Title",
                "RegistrationDateTime",
                "StartDateTime",
                "EndDateTime",
                "Importance",
                "Urgency",
                "Percentage",
                "Description",
                "Author",
                "Category",
            ).joinToString(",")
        val rows =
            tasksList.values.joinToString("\n") { task ->
                listOf(
                    task.id,
                    task.title,
                    task.registrationDateTime,
                    task.startDateTime,
                    task.endDateTime ?: "",
                    task.importance.type,
                    task.urgency,
                    task.percentage,
                    task.description,
                    task.author,
                    task.categoryId,
                ).joinToString(",") { it.toString() }
            }
        return "$header\n$rows"
    }
}
