package it.unibo.coordination.utils

import java.util.*
import java.util.stream.Stream

fun <T> Optional<T>.asStream(): Stream<T> =
        if (isPresent) {
            Stream.of(get())
        } else {
            Stream.empty()
        }