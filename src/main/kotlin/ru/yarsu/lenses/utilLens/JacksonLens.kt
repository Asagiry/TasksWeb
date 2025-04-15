package ru.yarsu.lenses.utilLens

import org.http4k.core.Body
import org.http4k.format.Jackson.auto

val jsonBodyLens = Body.auto<Any>().toLens()
