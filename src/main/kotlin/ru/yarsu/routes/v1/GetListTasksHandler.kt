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
import ru.yarsu.operations.v1.GetListTasksOperation
import java.io.StringWriter

class GetListTasksHandler(
    private val operation: GetListTasksOperation,
) : HttpHandler {
    override fun invoke(request: Request): Response {
        val queryParameters: Parameters = request.uri.queries()
        val pageParam = queryParameters.findSingle("page")
        val recordsParam = queryParameters.findSingle("records-per-page")

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

        return Response(Status.OK)
            .contentType(ContentType.APPLICATION_JSON)
            .body(createBody(operation.get(page, records)))
    }

    private fun createBody(list: List<Task>): String {
        val stringWriter = StringWriter()
        val factory: JsonFactory = JsonFactoryBuilder().build()
        val outputGenerator: JsonGenerator = factory.createGenerator(stringWriter)
        outputGenerator.prettyPrinter = DefaultPrettyPrinter()
        with(outputGenerator) {
            writeStartArray()
            // Запись отсортированных задач в JSON
            list.forEach {
                writeStartObject()
                writeFieldName("Id")
                writeString(it.id.toString())
                writeFieldName("Title")
                writeString(it.title)
                writeFieldName("IsClosed")
                writeBoolean(it.percentage == 100)
                writeEndObject()
            }
            writeEndArray()
            close()
        }
        return stringWriter.toString()
    }
}
