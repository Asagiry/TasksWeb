package ru.yarsu.lenses.v2.tasks

import com.fasterxml.jackson.annotation.JsonProperty
import org.http4k.core.Body
import org.http4k.format.Jackson.auto
import java.time.LocalDateTime
import java.util.UUID

val PutTaskBodyLens = Body.auto<PutTaskBodyData>().toLens()

data class PutTaskBodyData(
    @JsonProperty("Title") val title: String,
    @JsonProperty("RegistrationDateTime") val registrationDateTime: LocalDateTime? = null,
    @JsonProperty("StartDateTime") val startDateTime: LocalDateTime? = null,
    @JsonProperty("EndDateTime") val endDateTime: LocalDateTime? = LocalDateTime.MIN,
    @JsonProperty("Importance") val importance: String? = null,
    @JsonProperty("Urgency") val urgency: Boolean? = null,
    @JsonProperty("Percentage") val percentage: Int? = null,
    @JsonProperty("Description") val description: String? = null,
    @JsonProperty("Author") val author: UUID,
    @JsonProperty("Category") val category: UUID?,
)
