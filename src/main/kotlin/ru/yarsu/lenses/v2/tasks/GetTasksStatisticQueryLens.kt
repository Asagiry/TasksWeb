package ru.yarsu.lenses.v2.tasks

import org.http4k.lens.Query
import org.http4k.lens.string

val GetTasksStatisticQueryLens =
    Query
        .string()
        .map { byParam ->
            require(byParam in listOf("registration", "start", "end")) {
                "Некорректное значение типа статистики. " +
                    "Для параметра by-date ожидается значение типа статистики, но получено пустое значение"
            }
            byParam
        }.required("by-date")
