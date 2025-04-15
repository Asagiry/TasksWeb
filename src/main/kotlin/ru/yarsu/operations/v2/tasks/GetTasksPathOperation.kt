package ru.yarsu.operations.v2.tasks

import ru.yarsu.classes.category.CategoryStorage
import ru.yarsu.classes.data.TaskFullData
import ru.yarsu.classes.task.TasksStorage
import ru.yarsu.classes.user.UsersStorage
import java.util.UUID

interface GetTasksPathOperation {
    fun get(id: UUID): TaskFullData?
}

class GetTasksPathOperationImpl(
    private val tasksStorage: TasksStorage,
    private val categoryStorage: CategoryStorage,
    private val usersStorage: UsersStorage,
) : GetTasksPathOperation {
    override fun get(id: UUID): TaskFullData? {
        val task = tasksStorage.getTask(id) ?: return null

        val user = usersStorage.getUser(task.author)!!
        val category =
            if (task.categoryId == null) {
                null
            } else {
                categoryStorage.getCategory(task.categoryId)
            }

        val newTaskFullData =
            TaskFullData(
                id = id,
                title = task.title,
                registrationDateTime = task.registrationDateTime,
                startDateTime = task.startDateTime,
                endDateTime = task.endDateTime,
                importance = task.importance.type,
                urgency = task.urgency,
                percentage = task.percentage,
                description = task.description,
                author = user.id,
                authorEmail = user.email,
                category = category?.id,
                categoryDescription = category?.description ?: "Без категории",
            )
        return newTaskFullData
    }
}
