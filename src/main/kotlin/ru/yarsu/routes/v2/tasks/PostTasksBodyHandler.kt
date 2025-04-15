package ru.yarsu.routes.v2.tasks

import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with
import ru.yarsu.ErrorJson
import ru.yarsu.classes.category.CategoryStorage
import ru.yarsu.classes.data.AuthorCategoryData
import ru.yarsu.classes.data.IdData
import ru.yarsu.classes.user.UsersStorage
import ru.yarsu.generateBadResponse
import ru.yarsu.lenses.utilLens.jsonBodyLens
import ru.yarsu.lenses.v2.tasks.PostTasksBodyLens
import ru.yarsu.operations.v2.tasks.PostTasksBodyOperation
import ru.yarsu.validateFields

class PostTasksBodyHandler(
    private val operation: PostTasksBodyOperation,
    private val categoryStorage: CategoryStorage,
    private val userStorage: UsersStorage,
) : HttpHandler {
    override fun invoke(request: Request): Response {
        try {
            val response = validateFields(request.bodyString(), categoryStorage, userStorage)
            if (response != null) {
                return response
            }
        } catch (e: com.fasterxml.jackson.core.JsonParseException) {
            return Response(Status.BAD_REQUEST)
                .with(
                    jsonBodyLens of
                        ErrorJson(
                            "{",
                            "Missing a name for object member.",
                        ),
                )
        }

        val postTaskBodyData = PostTasksBodyLens(request)
        try {
            val newUUID =
                operation.post(postTaskBodyData)
                    ?: return Response(Status.FORBIDDEN)
                        .with(
                            jsonBodyLens of
                                AuthorCategoryData(
                                    postTaskBodyData.author,
                                    postTaskBodyData.category!!,
                                ),
                        )
            return Response(Status.CREATED)
                .with(jsonBodyLens of IdData(newUUID))
        } catch (e: IllegalArgumentException) {
            if (e.message == "User") {
                return generateBadResponse("Юзер не найден")
            }
            if (e.message == "Category") {
                return generateBadResponse("Категория не найдена")
            }
            println(e.message)
            return Response(Status.BAD_REQUEST)
        }
    }
}
