package ru.yarsu.operations.v2.tasks

import ru.yarsu.classes.data.TaskImportantUrgentData
import ru.yarsu.classes.task.Task
import ru.yarsu.classes.task.TasksStorage
import ru.yarsu.sortByPagesRecords
import java.time.LocalDateTime

interface GetTasksByTimeQueryOperation {
    fun get(
        date: LocalDateTime,
        page: Int,
        records: Int,
    ): List<TaskImportantUrgentData>
}

class GetTasksByTimeQueryOperationImpl(
    private val tasksStorage: TasksStorage,
) : GetTasksByTimeQueryOperation {
    override fun get(
        date: LocalDateTime,
        page: Int,
        records: Int,
    ): List<TaskImportantUrgentData> {
        val list =
            tasksStorage
                .getList()
                .filter { it.startDateTime.isBefore(date) && it.percentage < 100 } // Фильтрация по дате и проценту
                .sortedWith(
                    compareByDescending<Task> { it.importance }
                        .thenByDescending { it.urgency }
                        .thenBy { it.registrationDateTime }
                        .thenBy { it.id },
                ).map { task ->
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
