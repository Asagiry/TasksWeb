package ru.yarsu.operations.v1

import ru.yarsu.classes.task.Task
import ru.yarsu.classes.task.TasksStorage
import ru.yarsu.sortByPagesRecords

interface GetListEisenhowerOperation {
    fun get(
        importance: Boolean?,
        urgency: Boolean?,
        page: Int,
        records: Int,
    ): List<Task>
}

class GetListEisenhowerOperationImpl(
    private val tasksStorage: TasksStorage,
) : GetListEisenhowerOperation {
    override fun get(
        importance: Boolean?,
        urgency: Boolean?,
        page: Int,
        records: Int,
    ): List<Task> {
        val list =
            tasksStorage
                .getList()
                .sortedWith(
                    compareBy<Task> { it.registrationDateTime }
                        .thenBy { it.id },
                ).filter {
                    (importance?.let { imp -> it.importance.priority == imp } ?: true) &&
                        (urgency?.let { urg -> it.urgency == urg } ?: true)
                }
        return sortByPagesRecords(page, records, list)
    }
}
