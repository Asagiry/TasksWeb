package ru.yarsu.classes.data

enum class Importance(
    val type: String,
    val priority: Boolean,
) {
    VERYLOW("очень низкий", false),
    LOW("низкий", false),
    CASUAL("обычный", false),
    HIGH("высокий", true),
    VERYHIGH("очень высокий", true),
    CRITICAL("критический", true),
    ;

    companion object {
        fun fromType(type: String): Importance? = entries.find { it.type == type }
    }
}
