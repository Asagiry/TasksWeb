package ru.yarsu.routes.v2.tasks

import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with
import ru.yarsu.lenses.utilLens.jsonBodyLens
import ru.yarsu.lenses.utilLens.paginationParametersLens
import ru.yarsu.operations.v2.tasks.GetTasksOperation

class GetTasksHandler(
    private val operation: GetTasksOperation,
) : HttpHandler {
    override fun invoke(request: Request): Response {
        val parameters = paginationParametersLens(request)
        val page = parameters.page
        val records = parameters.records
        return Response(Status.OK).with(jsonBodyLens of operation.get(page, records))
    }
}
