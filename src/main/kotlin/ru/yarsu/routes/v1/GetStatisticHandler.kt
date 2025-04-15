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
import ru.yarsu.generateBadResponse
import ru.yarsu.operations.v1.GetStatisticOperationImpl
import java.io.StringWriter

class GetStatisticHandler(
    private val operation: GetStatisticOperationImpl,
) : HttpHandler {
    override fun invoke(request: Request): Response {
        val queryParameters: Parameters = request.uri.queries()
        val byParam = queryParameters.findSingle("by-date") ?: return generateBadResponse("Отсутствует параметр by-date")

        if (byParam !in listOf("registration", "start", "end")) {
            return generateBadResponse(
                "Некорректное значение типа статистики.",
                " Для параметра by-date ожидается значение типа статистики, но получено пустое значение",
            )
        }

        return Response(Status.OK)
            .contentType(ContentType.APPLICATION_JSON)
            .body(createBody(operation.get(byParam)))
    }

    private fun createBody(parsed: Map<String, Map<String, Int>>): String {
        val stringWriter = StringWriter()
        val factory: JsonFactory = JsonFactoryBuilder().build()
        val outputGenerator: JsonGenerator = factory.createGenerator(stringWriter)
        outputGenerator.prettyPrinter = DefaultPrettyPrinter()
        println(parsed)
        with(outputGenerator) {
            writeStartObject()
            val type = parsed.keys.first()
            writeFieldName(type)
            writeStartObject()
            val list = parsed[type]
            list!!.forEach {
                writeFieldName(it.key)
                writeNumber(it.value)
            }
            writeEndObject()
            writeEndObject()
            close()
        }
        return stringWriter.toString()
    }
}
