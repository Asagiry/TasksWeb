package ru.yarsu.classes.category

import ru.yarsu.classes.data.Color
import java.util.UUID

data class Category(
    val id: UUID,
    val description: String,
    val color: Color,
    val owner: UUID?,
)
