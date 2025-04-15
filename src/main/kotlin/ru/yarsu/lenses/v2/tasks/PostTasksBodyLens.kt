package ru.yarsu.lenses.v2.tasks

import com.fasterxml.jackson.annotation.JsonProperty
import org.http4k.core.Body
import org.http4k.format.Jackson.auto
import java.time.LocalDateTime
import java.util.UUID

val PostTasksBodyLens = Body.auto<PostTasksBodyData>().toLens()

data class PostTasksBodyData(
    @JsonProperty("Title") val title: String,
    @JsonProperty("RegistrationDateTime") val registrationDateTime: LocalDateTime = LocalDateTime.now(),
    @JsonProperty("StartDateTime") val startDateTime: LocalDateTime = registrationDateTime,
    @JsonProperty("EndDateTime") val endDateTime: LocalDateTime? = null,
    @JsonProperty("Importance") val importance: String = "обычный",
    @JsonProperty("Urgency") val urgency: Boolean = false,
    @JsonProperty("Percentage") val percentage: Int = 0,
    @JsonProperty("Description") val description: String = "",
    @JsonProperty("Author") val author: UUID,
    @JsonProperty("Category") val category: UUID?,
)
