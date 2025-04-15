package ru.yarsu

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.core.JsonFactoryBuilder
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.http4k.core.ContentType
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with
import org.http4k.lens.contentType
import ru.yarsu.classes.category.CategoryStorage
import ru.yarsu.classes.data.Importance
import ru.yarsu.classes.user.UsersStorage
import ru.yarsu.lenses.utilLens.jsonBodyLens
import java.io.StringWriter
import java.time.LocalDateTime
import java.time.format.DateTimeParseException
import java.util.UUID

fun checkPageRecords(
    pageParam: String?,
    recordsParam: String?,
): Map<String, Int?> {
    val page =
        try {
            val pageNumber = pageParam?.toInt() ?: 1
            if (pageNumber < 1) throw NumberFormatException()
            pageNumber
        } catch (e: NumberFormatException) {
            null
        }

    val records =
        try {
            val recordsNumber = recordsParam?.toInt() ?: 10
            if (recordsNumber !in setOf(5, 10, 20, 50)) throw NumberFormatException()
            recordsNumber
        } catch (e: NumberFormatException) {
            null
        }

    return mapOf("Page" to page, "Records" to records)
}

fun generateBadResponse(
    messageError: String,
    messageAwait: String = "",
): Response {
    val stringWriter = StringWriter()
    val factory: JsonFactory = JsonFactoryBuilder().build()
    val outputGenerator: JsonGenerator = factory.createGenerator(stringWriter)
    outputGenerator.prettyPrinter = DefaultPrettyPrinter()
    with(outputGenerator) {
        writeStartObject()
        writeFieldName("error")
        writeString(
            "$messageError$messageAwait",
        )
        writeEndObject()
        close()
    }
    return Response(Status.BAD_REQUEST).contentType(ContentType.APPLICATION_JSON).body(stringWriter.toString())
}

fun generateNotFoundResponse(
    fieldName: String,
    fieldValue: String,
    message: String,
): Response {
    val stringWriter = StringWriter()
    val factory: JsonFactory = JsonFactoryBuilder().build()
    val outputGenerator: JsonGenerator = factory.createGenerator(stringWriter)
    outputGenerator.prettyPrinter = DefaultPrettyPrinter()
    with(outputGenerator) {
        writeStartObject()
        writeFieldName(fieldName)
        writeString(fieldValue)
        writeFieldName("Error")
        writeString(message)
        writeEndObject()
        close()
    }
    return Response(Status.NOT_FOUND)
        .body(stringWriter.toString())
}

fun <T> sortByPagesRecords(
    page: Int,
    records: Int,
    list: List<T>,
): List<T> {
    val fromIndex = (page - 1) * records
    if (fromIndex >= list.size) {
        return emptyList()
    }
    val toIndex = (fromIndex + records).coerceAtMost(list.size)
    return list.subList(fromIndex, toIndex)
}

fun validateFields(
    body: String,
    categoryStorage: CategoryStorage,
    userStorage: UsersStorage,
): Response? {
    val errors = mutableMapOf<String, FieldErrorResponse>()
    val objectMapper = jacksonObjectMapper()
    val rootNode: JsonNode = objectMapper.readTree(body)

    if (!rootNode.has("Title")) {
        errors["Title"] = FieldErrorResponse(null, "В теле запроса отсутствует поле Title")
    } else {
        val descriptionFieldNode = rootNode.get("Title")
        if (!descriptionFieldNode.isNull) {
            when {
                !descriptionFieldNode.isTextual ->
                    errors["Title"] = FieldErrorResponse(descriptionFieldNode, "Ожидается текст")
                descriptionFieldNode.isTextual && descriptionFieldNode.asText().isNullOrBlank() ->
                    errors["Title"] = FieldErrorResponse(descriptionFieldNode, "Ожидается текст")
            }
        } else {
            errors["Title"] = FieldErrorResponse(null, "Ожидается текст, получен null")
        }
    }
    if (rootNode.has("RegistrationDateTime")) {
        val dateFieldNode = rootNode.get("RegistrationDateTime")
        if (!dateFieldNode.isNull) {
            when {
                dateFieldNode.isTextual && !isValidDateTime(dateFieldNode.asText()) ->
                    errors["RegistrationDateTime"] = FieldErrorResponse(dateFieldNode, "Ожидается дата и время")

                !dateFieldNode.isTextual ->
                    errors["RegistrationDateTime"] =
                        FieldErrorResponse(dateFieldNode, "Ожидается строка даты и времени")
            }
        } else {
            errors["RegistrationDateTime"] = FieldErrorResponse(null, "Ожидается дата и время, но получен null")
        }
    }
    if (rootNode.has("StartDateTime")) {
        val dateFieldNode = rootNode.get("StartDateTime")
        if (!dateFieldNode.isNull) {
            when {
                dateFieldNode.isTextual && !isValidDateTime(dateFieldNode.asText()) ->
                    errors["StartDateTime"] = FieldErrorResponse(dateFieldNode, "Ожидается дата и время")

                !dateFieldNode.isTextual ->
                    errors["StartDateTime"] =
                        FieldErrorResponse(dateFieldNode, "Ожидается строка даты и времени")
            }
        } else {
            errors["StartDateTime"] = FieldErrorResponse(null, "Ожидается дата и время, но получен null")
        }
    }
    if (rootNode.has("EndDateTime")) {
        val dateFieldNode = rootNode.get("EndDateTime")
        if (!dateFieldNode.isNull) {
            when {
                dateFieldNode.isTextual && !isValidDateTime(dateFieldNode.asText()) ->
                    errors["EndDateTime"] = FieldErrorResponse(dateFieldNode, "Ожидается дата и время")

                !dateFieldNode.isTextual ->
                    errors["EndDateTime"] =
                        FieldErrorResponse(dateFieldNode, "Ожидается строка даты и времени")
            }
        }
    }

    if (rootNode.has("Importance")) {
        val importanceFieldNode = rootNode.get("Importance")
        if (!importanceFieldNode.isNull) {
            when {
                !importanceFieldNode.isTextual ->
                    errors["Importance"] = FieldErrorResponse(importanceFieldNode, "Ожидается приоритет за списка")

                !isValidImportance(importanceFieldNode.asText()) ->
                    errors["Importance"] = FieldErrorResponse(importanceFieldNode, "Ожидается приоритет за списка")
            }
        } else {
            errors["Importance"] = FieldErrorResponse(null, "Ожидается Importance, получен null")
        }
    }

    if (rootNode.has("Urgency")) {
        val urgencyFieldNode = rootNode.get("Urgency")
        if (!urgencyFieldNode.isNull) {
            when {
                !urgencyFieldNode.isBoolean ->
                    errors["Urgency"] = FieldErrorResponse(urgencyFieldNode, "Ожидается булево значение")
            }
        } else {
            errors["Urgency"] = FieldErrorResponse(null, "Ожидается Urgency, получен null")
        }
    }

    if (rootNode.has("Percentage")) {
        val percentageFieldNode = rootNode.get("Percentage")
        if (!percentageFieldNode.isNull) {
            when {
                !percentageFieldNode.isInt ->
                    errors["Percentage"] = FieldErrorResponse(percentageFieldNode, "Ожидается целое число")

                percentageFieldNode.asInt() !in 0..100 ->
                    errors["Percentage"] =
                        FieldErrorResponse(
                            percentageFieldNode,
                            "Ожидается натуральное число от 0 до 100",
                        )
            }
        } else {
            errors["Percentage"] = FieldErrorResponse(null, "Ожидается Percentage, получен null")
        }
    }

    if (rootNode.has("Description")) {
        val descriptionFieldNode = rootNode.get("Description")
        if (!descriptionFieldNode.isNull) {
            when {
                !descriptionFieldNode.isTextual ->
                    errors["Description"] = FieldErrorResponse(descriptionFieldNode, "Ожидается строка")
            }
        } else {
            errors["Description"] = FieldErrorResponse(null, "Ожидается строка, получен null")
        }
    }

    if (!rootNode.has("Author")) {
        errors["Author"] = FieldErrorResponse(null, "В теле запроса отсутствует поле Author")
    } else {
        val authorFieldNode = rootNode.get("Author")
        if (!authorFieldNode.isNull) {
            when {
                !authorFieldNode.isTextual ->
                    errors["Author"] = FieldErrorResponse(authorFieldNode, "Ожидается строка UUID")
                !isValidUUID(authorFieldNode.asText()) ->
                    errors["Author"] = FieldErrorResponse(authorFieldNode, "Ожидается валидный UUID")
                userStorage.getUser(UUID.fromString(authorFieldNode.asText())) == null ->
                    errors["Author"] = FieldErrorResponse(authorFieldNode, "Такого автора нет")
            }
        } else {
            errors["Author"] = FieldErrorResponse(null, "Ожидается Author, получен null")
        }
    }

    if (!rootNode.has("Category")) {
        errors["Category"] = FieldErrorResponse(null, "В теле запроса отсутствует поле Category")
    } else {
        val categoryFieldNode = rootNode.get("Category")
        if (!categoryFieldNode.isNull) {
            when {
                !(categoryFieldNode.isTextual || categoryFieldNode.isNull) ->
                    errors["Category"] = FieldErrorResponse(categoryFieldNode, "Ожидается строка UUID или null")
                categoryFieldNode.isTextual && !isValidUUID(categoryFieldNode.asText()) ->
                    errors["Category"] = FieldErrorResponse(categoryFieldNode, "Ожидается валидный UUID")
                categoryStorage.getCategory(UUID.fromString(categoryFieldNode.asText())) == null ->
                    errors["Category"] = FieldErrorResponse(categoryFieldNode, "Такого автора нет")
            }
        }
    }

    if (errors.isEmpty()) return null
    return Response(Status.BAD_REQUEST).with(
        jsonBodyLens of errors,
    )
}

private fun isValidDateTime(asText: String?): Boolean {
    if (asText.isNullOrBlank()) return false
    return try {
        LocalDateTime.parse(asText)
        true
    } catch (e: DateTimeParseException) {
        false
    }
}

private fun isValidImportance(importanceType: String): Boolean = Importance.fromType(importanceType) != null

private fun isValidUUID(value: String): Boolean =
    try {
        UUID.fromString(value)
        true
    } catch (_: IllegalArgumentException) {
        false
    }

data class FieldErrorResponse(
    @JsonProperty("Value") val value: Any?,
    @JsonProperty("Error") val error: String,
)

data class ErrorJson(
    @JsonProperty("Value") val value: String,
    @JsonProperty("Error") val error: String,
)
