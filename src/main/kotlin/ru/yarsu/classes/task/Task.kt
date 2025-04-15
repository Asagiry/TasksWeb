package ru.yarsu.classes.task

import ru.yarsu.classes.data.Importance
import java.time.LocalDateTime
import java.util.UUID

const val CLOSED_TASK = 100

data class Task(
    val id: UUID,
    val title: String,
    val registrationDateTime: LocalDateTime,
    val startDateTime: LocalDateTime,
    val endDateTime: LocalDateTime?,
    val importance: Importance,
    val urgency: Boolean,
    val percentage: Int,
    val description: String,
    val author: UUID,
    val categoryId: UUID?,
) {
    val isClosed: Boolean
        get() = percentage == CLOSED_TASK
}
