package ru.yarsu.lenses.v2.tasks

import org.http4k.lens.Path
import org.http4k.lens.uuid

val GetTasksPathLens = Path.uuid().of("task-id")
