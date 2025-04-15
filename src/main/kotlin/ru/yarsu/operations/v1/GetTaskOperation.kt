package ru.yarsu.operations.v1

import ru.yarsu.classes.task.Task
import ru.yarsu.classes.task.TasksStorage
import ru.yarsu.classes.user.User
import ru.yarsu.classes.user.UsersStorage
import java.util.UUID

interface GetTaskOperation {
    fun get(id: UUID): Map<Task, User>?
}

class GetTaskOperationImpl(
    private val tasksStorage: TasksStorage,
    private val usersStorage: UsersStorage,
) : GetTaskOperation {
    override fun get(id: UUID): Map<Task, User>? {
        val task = tasksStorage.getTask(id) ?: return null
        val user = usersStorage.getUser(task.author) ?: return null
        return mapOf(task to user)
    }
}
