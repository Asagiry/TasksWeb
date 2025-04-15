package ru.yarsu.routes.v2.tasks

import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with
import ru.yarsu.ErrorJson
import ru.yarsu.classes.category.CategoryStorage
import ru.yarsu.classes.data.AuthorCategoryData
import ru.yarsu.classes.user.UsersStorage
import ru.yarsu.generateBadResponse
import ru.yarsu.generateNotFoundResponse
import ru.yarsu.lenses.utilLens.jsonBodyLens
import ru.yarsu.lenses.v2.tasks.GetTasksPathLens
import ru.yarsu.lenses.v2.tasks.PutTaskBodyLens
import ru.yarsu.operations.v2.tasks.PutTaskBodyOperation
import ru.yarsu.validateFields

class PutTaskBodyHandler(
    private val operation: PutTaskBodyOperation,
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
        val id = GetTasksPathLens(request)
        val putTaskBodyData = PutTaskBodyLens(request)
        try {
            operation.put(id, putTaskBodyData)
        } catch (e: IllegalArgumentException) {
            if (e.message == "Forbidden") {
                return Response(Status.FORBIDDEN)
                    .with(
                        jsonBodyLens of
                            AuthorCategoryData(
                                putTaskBodyData.author,
                                categoryStorage.getCategory(putTaskBodyData.category!!)!!.owner!!,
                            ),
                    )
            }
            if (e.message == "NotFound") {
                return generateNotFoundResponse(
                    fieldName = "TaskId",
                    fieldValue = id.toString(),
                    message = "Задача не найдена",
                )
            }
            if (e.message == "User") {
                return generateBadResponse("BadUser")
            }
            if (e.message == "Category") {
                return generateBadResponse("BadCategory")
            }
        }
        return Response(Status.NO_CONTENT)
    }
}
