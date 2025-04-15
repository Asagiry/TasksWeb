package ru.yarsu.classes.data

enum class Color(
    val colorName: String,
    val rgb: Int,
) {
    BLACK("Чёрный", 0x000000),
    BLUE("Синий", 0x0000FF),
    CYAN("Голубой", 0x00FFFF),
    DARKGREEN("Тёмно-зелёный", 0x008000),
    GREEN("Зелёный", 0x00FF00),
    GRAY("Серый", 0x808080),
    MAGENTA("Пурпурный", 0xFF00FF),
    MAROON("Бордовый", 0x800000),
    OLIVE("Оливковый", 0x808000),
    PURPLE("Фиолетовый", 0x800080),
    RED("Красный", 0xFF0000),
    SILVER("Серебряный", 0xC0C0C0),
    TEAL("Бирюзовый", 0x008080),
    WHITE("Белый", 0xFFFFFF),
    YELLOW("Желтый", 0xFFFF00),
}
