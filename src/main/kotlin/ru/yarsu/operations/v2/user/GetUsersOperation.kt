package ru.yarsu.operations.v2.user

import ru.yarsu.classes.data.UserData
import ru.yarsu.classes.user.User
import ru.yarsu.classes.user.UsersStorage

interface GetUsersOperation {
    fun get(): List<UserData>
}

class GetUsersOperationImpl(
    private val usersStorage: UsersStorage,
) : GetUsersOperation {
    override fun get(): List<UserData> =
        usersStorage
            .getList()
            .sortedWith(
                compareBy<User> { it.login },
            ).map { user ->
                UserData(
                    id = user.id,
                    login = user.login,
                    registration = user.registrationDateTime,
                    email = user.email,
                )
            }
}
