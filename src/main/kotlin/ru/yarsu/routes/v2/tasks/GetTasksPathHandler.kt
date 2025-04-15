package ru.yarsu.routes.v2.tasks

import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with
import ru.yarsu.generateNotFoundResponse
import ru.yarsu.lenses.utilLens.jsonBodyLens
import ru.yarsu.lenses.v2.tasks.GetTasksPathLens
import ru.yarsu.operations.v2.tasks.GetTasksPathOperation

class GetTasksPathHandler(
    private val operation: GetTasksPathOperation,
) : HttpHandler {
    override fun invoke(request: Request): Response {
        val id = GetTasksPathLens(request)

        val triangleFullData =
            operation.get(id)
                ?: return generateNotFoundResponse(
                    fieldName = "TaskId",
                    fieldValue = id.toString(),
                    message = "Задача не найдена",
                )

        return Response(Status.OK)
            .with(jsonBodyLens of triangleFullData)
    }
}
