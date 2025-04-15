package ru.yarsu.lenses.v2.users

import org.http4k.lens.Path
import org.http4k.lens.uuid

val DelUsersPathLens = Path.uuid().of("user-id")
