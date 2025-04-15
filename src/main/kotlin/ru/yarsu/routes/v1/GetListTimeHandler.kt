package web.routes.v1

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.core.JsonFactoryBuilder
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import org.http4k.core.ContentType
import org.http4k.core.HttpHandler
import org.http4k.core.Parameters
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.findSingle
import org.http4k.core.queries
import org.http4k.lens.contentType
import ru.yarsu.checkPageRecords
import ru.yarsu.classes.task.Task
import ru.yarsu.generateBadResponse
import ru.yarsu.operations.v1.GetListTimeOperation
import java.io.StringWriter
import java.time.LocalDateTime

class GetListTimeHandler(
    private val operation: GetListTimeOperation,
) : HttpHandler {
    override fun invoke(request: Request): Response {
        val queryParameters: Parameters = request.uri.queries()
        val pageParam = queryParameters.findSingle("page")
        val recordsParam = queryParameters.findSingle("records-per-page")
        val timeParam = queryParameters.findSingle("time")

        val paramsList = checkPageRecords(pageParam, recordsParam)

        val page =
            paramsList["Page"]
                ?: return generateBadResponse(
                    "Некорректное значение параметра page. ",
                    "Ожидается натуральное число, но получено $pageParam",
                )
        val records =
            paramsList["Records"]
                ?: return generateBadResponse(
                    "Некорректное значение параметра records-per-page. ",
                    "Ожидается натуральное число, но получено $recordsParam",
                )

        if (timeParam == null) {
            return generateBadResponse(
                "Некорректное значение параметры time.",
                " Ожидается дата и время в формате ISO, но получена пустая строка",
            )
        }

        val time =
            try {
                LocalDateTime.parse(timeParam)
            } catch (_: Exception) {
                return generateBadResponse(
                    "Некорректное значение параметры time.",
                    " Ожидается дата и время в формате ISO, но получена $timeParam",
                )
            }

        return Response(Status.OK)
            .contentType(ContentType.APPLICATION_JSON)
            .body(createBody(operation.get(time, page, records)))
    }

    private fun createBody(list: List<Task>): String {
        val stringWriter = StringWriter()
        val factory: JsonFactory = JsonFactoryBuilder().build()
        val outputGenerator: JsonGenerator = factory.createGenerator(stringWriter)
        outputGenerator.prettyPrinter = DefaultPrettyPrinter()
        with(outputGenerator) {
            writeStartArray()
            list.forEach {
                writeStartObject()
                writeFieldName("Id")
                writeString(it.id.toString())
                writeFieldName("Title")
                writeString(it.title)
                writeFieldName("Importance")
                writeString(it.importance.type)
                writeFieldName("Urgency")
                writeBoolean(it.urgency)
                writeFieldName("Percentage")
                writeNumber(it.percentage)
                writeEndObject()
            }
            writeEndArray()
            close()
        }
        return stringWriter.toString()
    }
}
