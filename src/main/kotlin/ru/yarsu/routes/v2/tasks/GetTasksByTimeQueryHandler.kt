package ru.yarsu.routes.v2.tasks

import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with
import ru.yarsu.lenses.utilLens.jsonBodyLens
import ru.yarsu.lenses.utilLens.paginationParametersLens
import ru.yarsu.lenses.v2.tasks.GetTasksByTimeQueryLens
import ru.yarsu.operations.v2.tasks.GetTasksByTimeQueryOperation
import java.time.LocalDateTime

class GetTasksByTimeQueryHandler(
    private val operation: GetTasksByTimeQueryOperation,
) : HttpHandler {
    override fun invoke(request: Request): Response {
        val pageRecords = paginationParametersLens(request)
        val page = pageRecords.page
        val records = pageRecords.records

        val time: LocalDateTime = GetTasksByTimeQueryLens(request)

        return Response(Status.OK)
            .with(jsonBodyLens of operation.get(time, page, records))
    }
}
