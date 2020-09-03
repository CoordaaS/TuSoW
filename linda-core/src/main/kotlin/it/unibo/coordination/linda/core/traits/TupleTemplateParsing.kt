package it.unibo.coordination.linda.core.traits

import it.unibo.coordination.linda.core.Template
import it.unibo.coordination.linda.core.Tuple

interface TupleTemplateParsing<T : Tuple<T>, TT : Template<T>> {
    fun String.toTuple(): T

    fun String.toTemplate(): TT
}