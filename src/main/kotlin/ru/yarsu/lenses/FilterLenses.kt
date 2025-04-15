package ru.yarsu.lenses

import org.http4k.core.ContentType
import org.http4k.core.Filter
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.lens.LensFailure
import org.http4k.lens.contentType

fun jsonContentTypeFilter(): Filter =
    Filter { next: HttpHandler ->
        { request: Request ->
            val response = next(request)
            if (response.bodyString().isNotBlank()) {
                response.contentType(ContentType.APPLICATION_JSON)
            } else {
                response
            }
        }
    }

fun lensFailureFilter(): Filter =
    Filter { next: HttpHandler ->
        { request: Request ->
            try {
                next(request)
            } catch (lensFailure: LensFailure) {
                val message =
                    if (lensFailure.message!!.length > 50) {
                        lensFailure.message!!.take(50)
                    } else {
                        lensFailure.message
                    }

                Response(Status.BAD_REQUEST)
                    .body("{\"Error\":\"${message}\"}")
            }
        }
    }
