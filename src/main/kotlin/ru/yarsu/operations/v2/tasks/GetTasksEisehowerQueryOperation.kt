package ru.yarsu.operations.v2.tasks

import ru.yarsu.classes.data.TaskImportantUrgentData
import ru.yarsu.classes.task.Task
import ru.yarsu.classes.task.TasksStorage
import ru.yarsu.sortByPagesRecords

interface GetTasksEisehowerQueryOperation {
    fun get(
        importance: Boolean?,
        urgency: Boolean?,
        page: Int,
        records: Int,
    ): List<TaskImportantUrgentData>
}

class GetTasksEisenhowerQueryOperationImpl(
    private val tasksStorage: TasksStorage,
) : GetTasksEisehowerQueryOperation {
    override fun get(
        importance: Boolean?,
        urgency: Boolean?,
        page: Int,
        records: Int,
    ): List<TaskImportantUrgentData> {
        val list =
            tasksStorage
                .getList()
                .sortedWith(
                    compareBy<Task> { it.registrationDateTime }
                        .thenBy { it.id },
                ).filter {
                    (importance?.let { imp -> it.importance.priority == imp } ?: true) &&
                        (urgency?.let { urg -> it.urgency == urg } ?: true)
                }.map { task ->
                    TaskImportantUrgentData(
                        id = task.id,
                        title = task.title,
                        importance = task.importance.type,
                        urgency = task.urgency,
                        percentage = task.percentage,
                    )
                }
        return sortByPagesRecords(page, records, list)
    }
}
