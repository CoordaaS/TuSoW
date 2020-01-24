package it.unibo.coordination.utils

import org.apache.commons.collections4.MultiSet
import org.apache.commons.collections4.multiset.HashMultiSet
import java.util.stream.Stream

fun <X> Stream<X>.toMultiSet(): MultiSet<X> {
    val result = HashMultiSet<X>()
    forEach {
        result.add(it)
    }
    return result
}

fun <X> Iterable<X>.toMultiSet(): MultiSet<X> {
    val result = HashMultiSet<X>()
    forEach {
        result.add(it)
    }
    return result
}

fun <X> Sequence<X>.toMultiSet(): MultiSet<X> =
        this.asIterable().toMultiSet()

fun <X> emptyMultiSet(): MultiSet<X> = HashMultiSet()

fun <X> multiSetOf(vararg items: X): MultiSet<X> = HashMultiSet<X>().also {
    for (item in items) {
        it.add(item)
    }
}