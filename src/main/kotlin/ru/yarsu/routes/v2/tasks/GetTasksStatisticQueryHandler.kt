package ru.yarsu.routes.v2.tasks

import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with
import ru.yarsu.lenses.utilLens.jsonBodyLens
import ru.yarsu.lenses.v2.tasks.GetTasksStatisticQueryLens
import ru.yarsu.operations.v2.tasks.GetTasksStatisticQueryOperation

class GetTasksStatisticQueryHandler(
    private val operation: GetTasksStatisticQueryOperation,
) : HttpHandler {
    override fun invoke(request: Request): Response {
        val statistic = GetTasksStatisticQueryLens(request)

        return Response(Status.OK)
            .with(jsonBodyLens of operation.get(statistic))
    }
}
