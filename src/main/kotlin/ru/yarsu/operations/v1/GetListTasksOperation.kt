package ru.yarsu.operations.v1

import ru.yarsu.classes.task.Task
import ru.yarsu.classes.task.TasksStorage
import ru.yarsu.sortByPagesRecords

interface GetListTasksOperation {
    fun get(
        page: Int,
        records: Int,
    ): List<Task>
}

class GetListTasksOperationImpl(
    private val tasksStorage: TasksStorage,
) : GetListTasksOperation {
    override fun get(
        page: Int,
        records: Int,
    ): List<Task> {
        val list = tasksStorage.getList().sortedWith(compareBy<Task> { it.registrationDateTime }.thenBy { it.id })
        return sortByPagesRecords(page, records, list)
    }
}
