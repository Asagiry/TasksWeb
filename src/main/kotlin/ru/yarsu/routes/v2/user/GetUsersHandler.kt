package ru.yarsu.routes.v2.user

import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with
import ru.yarsu.lenses.utilLens.jsonBodyLens
import ru.yarsu.operations.v2.user.GetUsersOperation

class GetUsersHandler(
    private val operation: GetUsersOperation,
) : HttpHandler {
    override fun invoke(request: Request): Response =
        Response(Status.OK)
            .with(jsonBodyLens of operation.get())
}
