package ru.yarsu.routes.v2.tasks

import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with
import ru.yarsu.lenses.utilLens.jsonBodyLens
import ru.yarsu.lenses.utilLens.paginationParametersLens
import ru.yarsu.lenses.v2.tasks.ImportantUrgentLens
import ru.yarsu.operations.v2.tasks.GetTasksEisehowerQueryOperation

class GetTasksEisenhowerQueryHandler(
    private val operation: GetTasksEisehowerQueryOperation,
) : HttpHandler {
    override fun invoke(request: Request): Response {
        val pageRecords = paginationParametersLens(request)
        val page = pageRecords.page
        val records = pageRecords.records

        val importantUrgent = ImportantUrgentLens(request)
        val important = importantUrgent.important
        val urgent = importantUrgent.urgent

        return Response(Status.OK)
            .with(jsonBodyLens of operation.get(important, urgent, page, records))
    }
}
