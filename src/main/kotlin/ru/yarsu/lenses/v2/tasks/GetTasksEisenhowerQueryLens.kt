package ru.yarsu.lenses.v2.tasks

import org.http4k.lens.LensFailure
import org.http4k.lens.Query
import org.http4k.lens.boolean
import org.http4k.lens.composite

val importantLens = Query.boolean().optional("important")

val urgentLens = Query.boolean().optional("urgent")

val ImportantUrgentLens =
    Query.composite { request ->
        val important = importantLens(request)
        val urgent = urgentLens(request)

        if (important == null && urgent == null) {
            throw LensFailure(message = "Отсутствуют параметры important и urgent")
        }

        AreaParameters(important, urgent)
    }

data class AreaParameters(
    val important: Boolean?,
    val urgent: Boolean?,
)
