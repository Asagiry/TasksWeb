package ru.yarsu.operations.v2.category

import ru.yarsu.classes.category.Category
import ru.yarsu.classes.category.CategoryStorage
import ru.yarsu.classes.data.CategoryData
import ru.yarsu.classes.user.UsersStorage

interface GetCategoryOperation {
    fun get(): List<CategoryData>
}

class GetCategoryOperationImpl(
    private val categoryStorage: CategoryStorage,
    private val usersStorage: UsersStorage,
) : GetCategoryOperation {
    override fun get(): List<CategoryData> {
        val list =
            categoryStorage
                .getList()
                .sortedWith(
                    compareBy<Category> { it.description }
                        .thenBy { it.id },
                ).map { category ->
                    CategoryData(
                        id = category.id,
                        description = category.description,
                        color = category.color,
                        owner = category.owner,
                        ownerName = if (category.owner == null) "Общая" else usersStorage.getUser(category.owner)!!.login,
                    )
                }
        return list
    }
}
