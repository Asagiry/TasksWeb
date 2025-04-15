package ru.yarsu.lenses.v2.category

import org.http4k.lens.Path
import org.http4k.lens.uuid

val PutCategoryPathLens = Path.uuid().of("category-id")
