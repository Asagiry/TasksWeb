package ru.yarsu.classes.category

import java.util.UUID

class CategoryStorage(
    private val list: List<Category>,
) {
    private val categoryList = list.associateBy { it.id }.toMutableMap()

    fun getList(): List<Category> = categoryList.values.toList()

    fun addCategory(category: Category) {
        categoryList[category.id] = category
    }

    fun getCategory(id: UUID): Category? = categoryList[id]

    fun removeCategory(id: UUID) {
        categoryList.remove(id)
    }

    fun toCsv(): String {
        val header =
            listOf(
                "Id",
                "Description",
                "Color",
                "Owner",
            ).joinToString(",")
        val rows =
            categoryList.values.joinToString("\n") { category ->
                listOf(
                    category.id,
                    category.description,
                    category.color.name,
                    category.owner,
                ).joinToString(",") { it.toString() }
            }
        return "$header\n$rows"
    }
}
