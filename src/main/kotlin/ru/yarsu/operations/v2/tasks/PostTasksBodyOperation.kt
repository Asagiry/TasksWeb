package ru.yarsu.operations.v2.tasks

import ru.yarsu.classes.category.CategoryStorage
import ru.yarsu.classes.data.Importance
import ru.yarsu.classes.task.Task
import ru.yarsu.classes.task.TasksStorage
import ru.yarsu.classes.user.UsersStorage
import ru.yarsu.lenses.v2.tasks.PostTasksBodyData
import java.util.UUID

interface PostTasksBodyOperation {
    fun post(postTrianglesBodyData: PostTasksBodyData): UUID?
}

class PostTasksBodyOperationImpl(
    private val tasksStorage: TasksStorage,
    private val categoryStorage: CategoryStorage,
    private val usersStorage: UsersStorage,
) : PostTasksBodyOperation {
    override fun post(postTrianglesBodyData: PostTasksBodyData): UUID? {
        val newTask =
            Task(
                id = UUID.randomUUID(),
                title = postTrianglesBodyData.title,
                registrationDateTime = postTrianglesBodyData.registrationDateTime,
                startDateTime = postTrianglesBodyData.startDateTime,
                endDateTime = postTrianglesBodyData.endDateTime,
                importance = Importance.fromType(postTrianglesBodyData.importance)!!,
                urgency = postTrianglesBodyData.urgency,
                percentage = postTrianglesBodyData.percentage,
                description = postTrianglesBodyData.description,
                author = postTrianglesBodyData.author,
                categoryId = postTrianglesBodyData.category,
            )

        val category =
            if (postTrianglesBodyData.category != null) {
                val category =
                    categoryStorage.getCategory(postTrianglesBodyData.category)
                        ?: throw IllegalArgumentException("Category with ID ${postTrianglesBodyData.category} not found")
                category
            } else {
                null
            }
        val author = usersStorage.getUser(postTrianglesBodyData.author) ?: throw IllegalArgumentException("User")

        if (category != null && category.owner != author.id) {
            return null
        }

        tasksStorage.addTask(newTask)

        return newTask.id
    }
}
