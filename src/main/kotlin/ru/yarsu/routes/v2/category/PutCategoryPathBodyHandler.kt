package ru.yarsu.routes.v2.category

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.core.JsonFactoryBuilder
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.body.formAsMap
import org.http4k.core.with
import ru.yarsu.generateNotFoundResponse
import ru.yarsu.lenses.utilLens.jsonBodyLens
import ru.yarsu.lenses.v2.category.PutCategoryPathLens
import ru.yarsu.operations.v2.category.PutCategoryPathBodyOperation
import java.io.StringWriter
import java.util.UUID

class PutCategoryPathBodyHandler(
    private val operation: PutCategoryPathBodyOperation,
) : HttpHandler {
    override fun invoke(request: Request): Response {
        val params = request.formAsMap()
        val description = params["Description"]?.firstOrNull()
        val owner = params["Owner"]?.firstOrNull()

        val wrongResponse = checkFields(description, owner)
        if (wrongResponse != null) {
            return wrongResponse
        }

        val category = PutCategoryPathLens(request)
        try {
            val list = operation.put(category, description!!, if (owner == "null") null else UUID.fromString(owner))
            if (list.isEmpty()) {
                return Response(Status.NO_CONTENT)
            } else {
                return Response(Status.FORBIDDEN)
                    .with(jsonBodyLens of list)
            }
        } catch (e: IllegalArgumentException) {
            if (e.message == "NotFoundCategory") {
                return generateNotFoundResponse(
                    "CategoryId",
                    category.toString(),
                    "Категория не найдена",
                )
            } else {
                val stringWriter = StringWriter()
                val factory: JsonFactory = JsonFactoryBuilder().build()
                val outputGenerator: JsonGenerator = factory.createGenerator(stringWriter)
                outputGenerator.prettyPrinter = DefaultPrettyPrinter()
                with(outputGenerator) {
                    writeStartObject()
                    writeFieldName("Owner")
                    writeStartObject()
                    writeFieldName("Value")
                    writeString(owner)
                    writeFieldName("Error")
                    writeString("Ожидается корректный UUID")
                    writeEndObject()
                    writeEndObject()
                    close()
                }
                return Response(Status.BAD_REQUEST)
                    .body(stringWriter.toString())
            }
        }
    }

    fun checkFields(
        description: String?,
        owner: String?,
    ): Response? {
        var wrongDescription = false
        var wrongOwner = false
        if (description == null || description == "null") {
            wrongDescription = true
        }

        if (owner == null) {
            wrongOwner = true
        }

        if (owner != "null" && !wrongOwner) {
            try {
                UUID.fromString(owner)
            } catch (_: IllegalArgumentException) {
                wrongOwner = true
            }
        }

        if (wrongOwner || wrongDescription) {
            val stringWriter = StringWriter()
            val factory: JsonFactory = JsonFactoryBuilder().build()
            val outputGenerator: JsonGenerator = factory.createGenerator(stringWriter)
            outputGenerator.prettyPrinter = DefaultPrettyPrinter()
            with(outputGenerator) {
                writeStartObject()
                if (wrongDescription) {
                    writeFieldName("Description")
                    writeStartObject()
                    writeFieldName("Value")
                    writeString("")
                    writeFieldName("Error")
                    writeString("Отсутствует поле")
                    writeEndObject()
                }
                if (wrongOwner) {
                    writeFieldName("Owner")
                    writeStartObject()
                    writeFieldName("Value")
                    writeString(owner)
                    writeFieldName("Error")
                    writeString("Ожидается корректный UUID")
                    writeEndObject()
                }
                writeEndObject()
                close()
            }
            return Response(Status.BAD_REQUEST)
                .body(stringWriter.toString())
        }
        return null
    }
}
