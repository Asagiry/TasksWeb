package ru.yarsu.operations.v2.user

import ru.yarsu.classes.category.CategoryStorage
import ru.yarsu.classes.data.CategoryIdDescriptionData
import ru.yarsu.classes.data.DeleteUserData
import ru.yarsu.classes.data.TaskIdTitleData
import ru.yarsu.classes.task.TasksStorage
import ru.yarsu.classes.user.UsersStorage
import java.util.UUID

interface DelUsersPathOperation {
    fun del(id: UUID): DeleteUserData
}

class DelUsersPathOperationImpl(
    private val tasksStorage: TasksStorage,
    private val categoryStorage: CategoryStorage,
    private val userStorage: UsersStorage,
) : DelUsersPathOperation {
    override fun del(id: UUID): DeleteUserData {
        val taskList = tasksStorage.getList()
        val categoryList = categoryStorage.getList()

        val taskIdTitleDataList = mutableListOf<TaskIdTitleData>()
        val categoryIdDescriptionDataList = mutableListOf<CategoryIdDescriptionData>()

        val user = userStorage.getUser(id) ?: throw IllegalArgumentException("NotFound")

        for (task in taskList) {
            if (task.author == user.id) {
                taskIdTitleDataList.add(
                    TaskIdTitleData(
                        task.id,
                        task.title,
                    ),
                )
            }
        }
        for (category in categoryList) {
            if (category.owner == user.id) {
                categoryIdDescriptionDataList.add(
                    CategoryIdDescriptionData(
                        category.id,
                        category.description,
                    ),
                )
            }
        }
        if (taskIdTitleDataList.isEmpty() && categoryIdDescriptionDataList.isEmpty()) {
            userStorage.removeUser(id)
        }
        return DeleteUserData(
            tasks =
                taskIdTitleDataList.sortedWith(
                    compareBy<TaskIdTitleData> { it.id },
                ),
            categories =
                categoryIdDescriptionDataList.sortedWith(
                    compareBy<CategoryIdDescriptionData> { it.id },
                ),
        )
    }
}
