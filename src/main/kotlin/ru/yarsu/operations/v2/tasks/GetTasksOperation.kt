package ru.yarsu.operations.v2.tasks

import ru.yarsu.classes.data.TaskShortData
import ru.yarsu.classes.task.Task
import ru.yarsu.classes.task.TasksStorage
import ru.yarsu.sortByPagesRecords

interface GetTasksOperation {
    fun get(
        page: Int,
        record: Int,
    ): List<TaskShortData>
}

class GetTasksOperationImpl(
    private val taskStorage: TasksStorage,
) : GetTasksOperation {
    override fun get(
        page: Int,
        record: Int,
    ): List<TaskShortData> {
        val list =
            taskStorage
                .getList()
                .sortedWith(
                    compareBy<Task> { it.registrationDateTime }
                        .thenBy { it.id },
                ).map { task ->
                    TaskShortData(
                        id = task.id,
                        title = task.title,
                        isClosed = task.isClosed,
                    )
                }
        return sortByPagesRecords(page, record, list)
    }
}
