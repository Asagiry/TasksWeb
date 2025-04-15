package ru.yarsu.lenses.utilLens

import org.http4k.lens.LensFailure
import org.http4k.lens.Query
import org.http4k.lens.composite
import org.http4k.lens.int
import ru.yarsu.classes.data.CORRECT_RECORDS
import ru.yarsu.classes.data.DEFAULT_PAGE
import ru.yarsu.classes.data.DEFAULT_RECORDS

val pageLens =
    Query
        .int()
        .map { page ->
            if (page < 1) {
                throw LensFailure(
                    message =
                        "Некорректное значение параметра page. " +
                            "Ожидается натуральное число, но получено $page",
                )
            }
            page
        }.defaulted("page", DEFAULT_PAGE)

val recordsLens =
    Query
        .int()
        .map { records ->
            if (records !in CORRECT_RECORDS) {
                throw LensFailure(
                    message =
                        "Некорректное значение параметра records-per-page. " +
                            "Ожидается натуральное число, но получено $records",
                )
            }
            records
        }.defaulted("records-per-page", DEFAULT_RECORDS)

val paginationParametersLens =
    Query.composite { request ->
        PaginationParameters(
            pageLens(request),
            recordsLens(request),
        )
    }

data class PaginationParameters(
    val page: Int,
    val records: Int,
)
