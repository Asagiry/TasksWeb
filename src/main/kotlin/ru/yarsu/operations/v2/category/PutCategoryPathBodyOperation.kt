package ru.yarsu.operations.v2.category

import ru.yarsu.classes.category.CategoryStorage
import ru.yarsu.classes.data.CategoryTaskData
import ru.yarsu.classes.task.TasksStorage
import ru.yarsu.classes.user.UsersStorage
import java.util.UUID

interface PutCategoryPathBodyOperation {
    fun put(
        categoryId: UUID,
        description: String,
        owner: UUID?,
    ): List<CategoryTaskData>
}

class PutCategoryPathBodyOperationImpl(
    private val tasksStorage: TasksStorage,
    private val categoryStorage: CategoryStorage,
    private val usersStorage: UsersStorage,
) : PutCategoryPathBodyOperation {
    override fun put(
        categoryId: UUID,
        description: String,
        owner: UUID?,
    ): List<CategoryTaskData> {
        val tasksList = tasksStorage.getList()
        val categoryList = categoryStorage.getList()
        val usersList = usersStorage.getList()
        val categoryTaskDataList = mutableListOf<CategoryTaskData>()

        val category = categoryStorage.getCategory(categoryId) ?: throw IllegalArgumentException("NotFoundCategory")
        if (owner != null && usersStorage.getUser(owner) == null) {
            throw IllegalArgumentException("NotFoundOwner")
        }

        var newCategory =
            category.copy(
                id = category.id,
                description = description,
                color = category.color,
                owner = owner,
            )

        // Редактируемая общая категория содержит задачи другого пользователя этой категории.
        //
        // Возвращает список задач редактируемой категории, принадлежащих другому пользователю.
        // Список упорядочен по возрастанию идентификатора автора и идентификатора задачи.

        if (owner != null) {
            for (task in tasksList) {
                if (task.author != owner) {
                    val user = usersStorage.getUser(task.author)!!
                    categoryTaskDataList.add(
                        CategoryTaskData(
                            task.id,
                            task.title,
                            user.id,
                            user.login,
                        ),
                    )
                }
            }
        }
        if (categoryTaskDataList.isNotEmpty()) {
            return categoryTaskDataList.sortedWith(
                compareBy<CategoryTaskData> { it.author }
                    .thenBy { it.taskId },
            )
        }

        categoryStorage.removeCategory(categoryId)

        categoryStorage.addCategory(newCategory)

        for (task in tasksList.filter { it.categoryId == newCategory.id }) {
            val newAuthor = if (newCategory.owner == null) task.author else newCategory.owner
            val newTask =
                task.copy(
                    author = newAuthor!!,
                    categoryId = newCategory.id,
                )
            tasksStorage.addTask(newTask)
        }

        return categoryTaskDataList
    }
}
