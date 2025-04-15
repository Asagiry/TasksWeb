package ru.yarsu.routes.v2.user

import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with
import ru.yarsu.generateNotFoundResponse
import ru.yarsu.lenses.utilLens.jsonBodyLens
import ru.yarsu.lenses.v2.users.DelUsersPathLens
import ru.yarsu.operations.v2.user.DelUsersPathOperation

class DelUsersPathHandler(
    private val operation: DelUsersPathOperation,
) : HttpHandler {
    override fun invoke(request: Request): Response {
        val id = DelUsersPathLens(request)
        return try {
            val delData = operation.del(id)
            if (delData.tasks.isEmpty() && delData.categories.isEmpty()) {
                Response(Status.NO_CONTENT)
            } else {
                Response(Status.FORBIDDEN)
                    .with(jsonBodyLens of delData)
            }
        } catch (_: IllegalArgumentException) {
            generateNotFoundResponse(
                "UserId",
                id.toString(),
                "Пользователь не найден",
            )
        }
    }
}
