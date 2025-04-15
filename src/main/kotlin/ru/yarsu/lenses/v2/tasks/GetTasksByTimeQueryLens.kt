package ru.yarsu.lenses.v2.tasks

import org.http4k.lens.LensFailure
import org.http4k.lens.Query
import org.http4k.lens.string
import java.time.LocalDateTime
import java.time.format.DateTimeParseException

val GetTasksByTimeQueryLens =
    Query
        .string()
        .map { timeParam ->
            try {
                LocalDateTime.parse(timeParam.toString()) // Converts the string into LocalDateTime
            } catch (e: DateTimeParseException) {
                throw LensFailure(
                    message = "Invalid time format. Expected format: 'yyyy-MM-ddTHH:mm:ss'.",
                )
            }
        }.required("time")
