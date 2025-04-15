package ru.yarsu.operations.v1

import ru.yarsu.classes.task.Task
import ru.yarsu.classes.task.TasksStorage
import ru.yarsu.sortByPagesRecords
import java.time.LocalDateTime

interface GetListTimeOperation {
    fun get(
        date: LocalDateTime,
        page: Int,
        records: Int,
    ): List<Task>
}

class GetListTimeOperationImpl(
    private val tasksStorage: TasksStorage,
) : GetListTimeOperation {
    override fun get(
        date: LocalDateTime,
        page: Int,
        records: Int,
    ): List<Task> {
        val list =
            tasksStorage
                .getList()
                .filter { it.startDateTime.isBefore(date) && it.percentage < 100 } // Фильтрация по дате и проценту
                .sortedWith(
                    compareByDescending<Task> { it.importance } // Убывание важности
                        .thenByDescending { it.urgency } // Убывание срочности
                        .thenBy { it.registrationDateTime } // Возрастание даты регистрации
                        .thenBy { it.id }, // Возрастание идентификатора
                )
        return sortByPagesRecords(page, records, list)
    }
}
