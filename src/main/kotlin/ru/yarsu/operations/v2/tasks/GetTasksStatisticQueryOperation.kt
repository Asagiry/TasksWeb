package ru.yarsu.operations.v2.tasks

import ru.yarsu.classes.data.StatisticData
import ru.yarsu.classes.data.TaskStatisticData
import ru.yarsu.classes.task.TasksStorage
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.Locale

interface GetTasksStatisticQueryOperation {
    fun get(statistic: String): TaskStatisticData
}

@Suppress("DEPRECATION")
class GetTasksStatisticQueryOperationImpl(
    private val tasksStorage: TasksStorage,
) : GetTasksStatisticQueryOperation {
    override fun get(statistic: String): TaskStatisticData {
        val tasksStorage = tasksStorage.getList()

        val parsed = mutableMapOf<String, Map<String, Int>>()

        var weekList = mutableMapOf<String, Int>()

        val orderedDays = DayOfWeek.entries.map { it.getDisplayName(TextStyle.FULL, Locale("ru")) }

        if (statistic.contains("registration")) {
            tasksStorage.forEach {
                val weekday = it.registrationDateTime.dayOfWeek
                val weekdayName =
                    weekday.getDisplayName(TextStyle.FULL, Locale("ru"))
                weekList[weekdayName] = weekList.getOrDefault(weekdayName, 0) + 1
            }
            parsed["statisticByRegistrationDateTime"] =
                weekList.toSortedMap(
                    compareBy {
                        if (it == "Не заполнено") {
                            Int.MAX_VALUE
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
                            Int.MAX_VALUE
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

        parsed.forEach {
            println(it.key)
            if (it.key == "statisticByRegistrationDateTime") {
                println("зашел в иф")
                println(it)
                return TaskStatisticData(
                    registration =
                        StatisticData(
                            monday = it.value["понедельник"],
                            tuesday = it.value["вторник"],
                            wednesday = it.value["среда"],
                            thursday = it.value["четверг"],
                            friday = it.value["пятница"],
                            saturday = it.value["суббота"],
                            sunday = it.value["воскресенье"],
                            none = null,
                        ),
                    null,
                    null,
                )
            } else if (it.key == "statisticByStartDateTime") {
                return TaskStatisticData(
                    null,
                    start =
                        StatisticData(
                            monday = it.value["понедельник"],
                            tuesday = it.value["вторник"],
                            wednesday = it.value["среда"],
                            thursday = it.value["четверг"],
                            friday = it.value["пятница"],
                            saturday = it.value["суббота"],
                            sunday = it.value["воскресенье"],
                            none = null,
                        ),
                    null,
                )
            } else {
                return TaskStatisticData(
                    null,
                    null,
                    end =
                        StatisticData(
                            monday = it.value["понедельник"],
                            tuesday = it.value["вторник"],
                            wednesday = it.value["среда"],
                            thursday = it.value["четверг"],
                            friday = it.value["пятница"],
                            saturday = it.value["суббота"],
                            sunday = it.value["воскресенье"],
                            none = it.value["Не заполнено"],
                        ),
                )
            }
        }
        return TaskStatisticData(
            null,
            null,
            null,
        )
    }
}
