package ru.yarsu.classes.user

import java.util.UUID

class UsersStorage(
    private val list: List<User>,
) {
    private val usersList = list.associateBy { it.id }.toMutableMap()

    fun getList(): List<User> = usersList.values.toList()

    fun addUser(user: User) {
        usersList[user.id] = user
    }

    fun getUser(id: UUID): User? = usersList[id]

    fun removeUser(id: UUID) {
        usersList.remove(id)
    }

    fun toCsv(): String {
        val header =
            listOf(
                "Id",
                "Login",
                "RegistrationDateTime",
                "Email",
            ).joinToString(",")
        val rows =
            usersList.values.joinToString("\n") { user ->
                listOf(
                    user.id,
                    user.login,
                    user.registrationDateTime,
                    user.email,
                ).joinToString(",") { it.toString() }
            }
        return "$header\n$rows"
    }

    fun checkLogin(login: String): Boolean = usersList.values.any { it.login == login }
}
