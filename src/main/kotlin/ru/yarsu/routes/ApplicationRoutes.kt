package web.routes

import org.http4k.core.ContentType
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.lens.contentType
import org.http4k.routing.bind
import org.http4k.routing.routes
import ru.yarsu.classes.category.CategoryStorage
import ru.yarsu.classes.task.TasksStorage
import ru.yarsu.classes.user.UsersStorage
import ru.yarsu.generateBadResponse
import ru.yarsu.lenses.jsonContentTypeFilter
import ru.yarsu.lenses.lensFailureFilter
import ru.yarsu.operations.v1.GetListEisenhowerOperationImpl
import ru.yarsu.operations.v1.GetListTasksOperationImpl
import ru.yarsu.operations.v1.GetListTimeOperationImpl
import ru.yarsu.operations.v1.GetStatisticOperationImpl
import ru.yarsu.operations.v1.GetTaskOperationImpl
import ru.yarsu.operations.v2.category.GetCategoryOperationImpl
import ru.yarsu.operations.v2.category.PutCategoryPathBodyOperationImpl
import ru.yarsu.operations.v2.tasks.GetTasksByTimeQueryOperationImpl
import ru.yarsu.operations.v2.tasks.GetTasksEisenhowerQueryOperationImpl
import ru.yarsu.operations.v2.tasks.GetTasksOperationImpl
import ru.yarsu.operations.v2.tasks.GetTasksPathOperationImpl
import ru.yarsu.operations.v2.tasks.GetTasksStatisticQueryOperationImpl
import ru.yarsu.operations.v2.tasks.PostTasksBodyOperationImpl
import ru.yarsu.operations.v2.tasks.PutTaskBodyOperationImpl
import ru.yarsu.operations.v2.user.DelUsersPathOperationImpl
import ru.yarsu.operations.v2.user.GetUsersOperationImpl
import ru.yarsu.routes.v2.category.GetCategoryHandler
import ru.yarsu.routes.v2.category.PutCategoryPathBodyHandler
import ru.yarsu.routes.v2.tasks.GetTasksByTimeQueryHandler
import ru.yarsu.routes.v2.tasks.GetTasksEisenhowerQueryHandler
import ru.yarsu.routes.v2.tasks.GetTasksHandler
import ru.yarsu.routes.v2.tasks.GetTasksPathHandler
import ru.yarsu.routes.v2.tasks.GetTasksStatisticQueryHandler
import ru.yarsu.routes.v2.tasks.PostTasksBodyHandler
import ru.yarsu.routes.v2.tasks.PutTaskBodyHandler
import ru.yarsu.routes.v2.user.DelUsersPathHandler
import ru.yarsu.routes.v2.user.GetUsersHandler
import web.routes.v1.GetListEisenhowerHandler
import web.routes.v1.GetListTasksHandler
import web.routes.v1.GetListTimeHandler
import web.routes.v1.GetStatisticHandler
import web.routes.v1.GetTaskHandler

fun routes(
    tasksStorage: TasksStorage,
    categoryStorage: CategoryStorage,
    usersStorage: UsersStorage,
): HttpHandler {
    val app =
        routes(
            "/ping" bind Method.GET to {
                Response(Status.OK)
                    .contentType(ContentType.APPLICATION_JSON)
                    .body("Приложение запущено")
            },
            "/v1/list-tasks" bind Method.GET to
                GetListTasksHandler(
                    GetListTasksOperationImpl(
                        tasksStorage,
                    ),
                ),
            "/v1/task/{task-id}" bind Method.GET to
                GetTaskHandler(
                    GetTaskOperationImpl(
                        tasksStorage,
                        usersStorage,
                    ),
                ),
            "/v1/task" bind Method.GET to
                { generateBadResponse("Отсутствует обязательный параметр task-id") },
            "/v1/list-eisenhower" bind Method.GET to
                GetListEisenhowerHandler(
                    GetListEisenhowerOperationImpl(
                        tasksStorage,
                    ),
                ),
            "/v1/list-time" bind Method.GET to
                GetListTimeHandler(
                    GetListTimeOperationImpl(
                        tasksStorage,
                    ),
                ),
            "/v1/statistic" bind Method.GET to
                GetStatisticHandler(
                    GetStatisticOperationImpl(
                        tasksStorage,
                    ),
                ),
            "v2/tasks" bind Method.GET to
                GetTasksHandler(
                    GetTasksOperationImpl(
                        tasksStorage,
                    ),
                ),
            "v2/tasks" bind Method.POST to
                PostTasksBodyHandler(
                    PostTasksBodyOperationImpl(
                        tasksStorage,
                        categoryStorage,
                        usersStorage,
                    ),
                    categoryStorage,
                    usersStorage,
                ),
            "v2/tasks/eisenhower" bind Method.GET to
                GetTasksEisenhowerQueryHandler(
                    GetTasksEisenhowerQueryOperationImpl(
                        tasksStorage,
                    ),
                ),
            "v2/tasks/by-time" bind Method.GET to
                GetTasksByTimeQueryHandler(
                    GetTasksByTimeQueryOperationImpl(
                        tasksStorage,
                    ),
                ),
            "v2/tasks/statistics" bind Method.GET to
                GetTasksStatisticQueryHandler(
                    GetTasksStatisticQueryOperationImpl(
                        tasksStorage,
                    ),
                ),
            "v2/tasks/{task-id}" bind Method.GET to
                GetTasksPathHandler(
                    GetTasksPathOperationImpl(
                        tasksStorage,
                        categoryStorage,
                        usersStorage,
                    ),
                ),
            "v2/tasks/{task-id}" bind Method.PUT to
                PutTaskBodyHandler(
                    PutTaskBodyOperationImpl(
                        tasksStorage,
                        categoryStorage,
                        usersStorage,
                    ),
                    categoryStorage,
                    usersStorage,
                ),
            "v2/categories" bind Method.GET to
                GetCategoryHandler(
                    GetCategoryOperationImpl(
                        categoryStorage,
                        usersStorage,
                    ),
                ),
            "v2/categories/{category-id}" bind Method.PUT to
                PutCategoryPathBodyHandler(
                    PutCategoryPathBodyOperationImpl(
                        tasksStorage,
                        categoryStorage,
                        usersStorage,
                    ),
                ),
            "v2/users" bind Method.GET to
                GetUsersHandler(
                    GetUsersOperationImpl(
                        usersStorage,
                    ),
                ),
            "/v2/users/{user-id}" bind Method.DELETE to
                DelUsersPathHandler(
                    DelUsersPathOperationImpl(
                        tasksStorage,
                        categoryStorage,
                        usersStorage,
                    ),
                ),
        )
    return jsonContentTypeFilter()(
        lensFailureFilter()(app),
    )
}
