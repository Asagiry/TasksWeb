package web.routes.v1

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.core.JsonFactoryBuilder
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import org.http4k.core.ContentType
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.lens.contentType
import org.http4k.routing.path
import ru.yarsu.classes.task.Task
import ru.yarsu.classes.user.User
import ru.yarsu.operations.v1.GetTaskOperation
import java.io.StringWriter
import java.util.UUID

class GetTaskHandler(
    private val operation: GetTaskOperation,
) : HttpHandler {
    override fun invoke(request: Request): Response = createResponse(request)

    private fun createResponse(request: Request): Response {
        val stringWriter = StringWriter()
        val taskId = request.path("task-id")
        val factory: JsonFactory = JsonFactoryBuilder().build()
        val outputGenerator: JsonGenerator = factory.createGenerator(stringWriter)
        outputGenerator.prettyPrinter = DefaultPrettyPrinter()
        val uuid =
            try {
                UUID.fromString(taskId)
            } catch (_: IllegalArgumentException) {
                with(outputGenerator) {
                    writeStartObject()
                    writeFieldName("error")
                    writeString(
                        "Некорректный идентификатор задачи." +
                            " Для параметра task-id ожидается UUID, но получено значение $taskId",
                    )
                    writeEndObject()
                    close()
                }
                return Response(Status.BAD_REQUEST)
                    .contentType(ContentType.APPLICATION_JSON)
                    .body(stringWriter.toString())
            }

        val outputMap: Map<Task, User>? = operation.get(uuid)
        if (outputMap == null) {
            with(outputGenerator) {
                writeStartObject()
                writeFieldName("task-id")
                writeString(taskId.toString())
                writeFieldName("error")
                writeString("Задача не найдена")
                writeEndObject()
                close()
            }
            return Response(Status.NOT_FOUND)
                .contentType(ContentType.APPLICATION_JSON)
                .body(stringWriter.toString())
        }
        val task = outputMap.keys.first()
        val user = outputMap.values.first()

        with(outputGenerator) {
            writeStartObject()
            writeFieldName("Id")
            writeString(task.id.toString())
            writeFieldName("Title")
            writeString(task.title.toString())
            writeFieldName("RegistrationDateTime")
            writeString(task.registrationDateTime.toString())
            writeFieldName("StartDateTime")
            writeString(task.startDateTime.toString())
            writeFieldName("EndDateTime")
            task.endDateTime?.let { writeString(task.endDateTime.toString()) } ?: writeNull()
            writeFieldName("Importance")
            writeString(task.importance.type)
            writeFieldName("Urgency")
            writeBoolean(task.urgency)
            writeFieldName("Percentage")
            writeNumber(task.percentage)
            writeFieldName("Description")
            writeString(task.description)
            writeFieldName("Author")
            writeString(user.id.toString())
            writeFieldName("AuthorEmail")
            writeString(user.email)
            writeEndObject()
            close()
        }
        return Response(Status.OK)
            .contentType(ContentType.APPLICATION_JSON)
            .body(stringWriter.toString())
    }
}
