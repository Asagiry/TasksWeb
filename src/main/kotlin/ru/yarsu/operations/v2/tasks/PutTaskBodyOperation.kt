package ru.yarsu.operations.v2.tasks

import ru.yarsu.classes.category.CategoryStorage
import ru.yarsu.classes.data.Importance
import ru.yarsu.classes.task.Task
import ru.yarsu.classes.task.TasksStorage
import ru.yarsu.classes.user.UsersStorage
import ru.yarsu.lenses.v2.tasks.PutTaskBodyData
import java.time.LocalDateTime
import java.util.UUID

interface PutTaskBodyOperation {
    fun put(
        id: UUID,
        putTaskBodyData: PutTaskBodyData,
    )
}

class PutTaskBodyOperationImpl(
    private val tasksStorage: TasksStorage,
    private val categoryStorage: CategoryStorage,
    private val usersStorage: UsersStorage,
) : PutTaskBodyOperation {
    override fun put(
        id: UUID,
        putTaskBodyData: PutTaskBodyData,
    ) {
        val task = tasksStorage.getTask(id) ?: throw IllegalArgumentException("NotFound")

        val category =
            if (putTaskBodyData.category != null) {
                val category =
                    categoryStorage.getCategory(putTaskBodyData.category)
                        ?: throw IllegalArgumentException("Category")
                category
            } else {
                null
            }
        val author = usersStorage.getUser(putTaskBodyData.author) ?: throw IllegalArgumentException("User")

        if (category != null && category.owner != author.id) {
            throw IllegalArgumentException("Forbidden")
        }

        val newTask =
            Task(
                id = task.id,
                title = putTaskBodyData.title,
                registrationDateTime = putTaskBodyData.registrationDateTime ?: task.registrationDateTime,
                startDateTime = putTaskBodyData.startDateTime ?: task.startDateTime,
                endDateTime = if (putTaskBodyData.endDateTime == LocalDateTime.MIN) task.endDateTime else putTaskBodyData.endDateTime,
                importance = putTaskBodyData.importance?.let { Importance.fromType(it) } ?: task.importance,
                urgency = putTaskBodyData.urgency ?: task.urgency,
                percentage = putTaskBodyData.percentage ?: task.percentage,
                description = putTaskBodyData.description ?: task.description,
                author = author.id,
                categoryId = category?.id,
            )
        tasksStorage.removeTask(id)
        tasksStorage.addTask(newTask)
    }
}
