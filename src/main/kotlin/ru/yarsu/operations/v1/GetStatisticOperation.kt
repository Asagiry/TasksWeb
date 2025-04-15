package ru.yarsu.operations.v1

import ru.yarsu.classes.task.TasksStorage
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.Locale

interface GetStatisticOperation {
    fun get(statistic: String): Map<String, Map<String, Int>>
}

@Suppress("DEPRECATION")
class GetStatisticOperationImpl(
    private val tasksStorage: TasksStorage,
) : GetStatisticOperation {
    override fun get(statistic: String): Map<String, Map<String, Int>> {
        val tasksStorage = tasksStorage.getList()

        val parsed = mutableMapOf<String, Map<String, Int>>()

        var weekList = mutableMapOf<String, Int>()

        val orderedDays = DayOfWeek.entries.map { it.getDisplayName(TextStyle.FULL, Locale("ru")) }

        if (statistic.contains("registration")) {
            tasksStorage.forEach {
                val weekday = it.registrationDateTime.dayOfWeek
                val weekdayName =
                    weekday.getDisplayName(TextStyle.FULL, Locale("ru")) // Полное название дня недели на русском
                weekList[weekdayName] = weekList.getOrDefault(weekdayName, 0) + 1
            }
            parsed["statisticByRegistrationDateTime"] =
                weekList.toSortedMap(
                    compareBy {
                        if (it == "Не заполнено") {
                            Int.MAX_VALUE // Перемещаем "Не заполнено" в конец
                        } else {
                            orderedDays.indexOf(it)
                        }
                    },
                )
        }

        if (statistic.contains("start")) {
            tasksStorage.forEach {
                val weekday = it.startDateTime.dayOfWeek
                val weekdayName = weekday.getDisplayName(TextStyle.FULL, Locale("ru"))
                weekList[weekdayName] = weekList.getOrDefault(weekdayName, 0) + 1
            }
            parsed["statisticByStartDateTime"] =
                weekList.toSortedMap(
                    compareBy {
                        if (it == "Не заполнено") {
                            Int.MAX_VALUE // Перемещаем "Не заполнено" в конец
                        } else {
                            orderedDays.indexOf(it)
                        }
                    },
                )
        }

        if (statistic.contains("end")) {
            tasksStorage.forEach {
                val weekday = it.endDateTime?.dayOfWeek
                val weekdayName: String?
                if (weekday != null) {
                    weekdayName = weekday.getDisplayName(TextStyle.FULL, Locale("ru"))
                } else {
                    weekdayName = null
                }
                if (weekday == null) {
                    weekList["Не заполнено"] = weekList.getOrDefault("Не заполнено", 0) + 1
                } else {
                    weekList[weekdayName!!] = weekList.getOrDefault(weekdayName, 0) + 1
                }
            }
            parsed["statisticByEndDateTime"] =
                weekList.toSortedMap(
                    compareBy {
                        if (it == "Не заполнено") {
                            Int.MAX_VALUE // Перемещаем "Не заполнено" в конец
                        } else {
                            orderedDays.indexOf(it)
                        }
                    },
                )
        }

        return parsed
    }
}
