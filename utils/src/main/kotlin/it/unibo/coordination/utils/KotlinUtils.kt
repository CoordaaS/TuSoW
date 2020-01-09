package it.unibo.coordination.utils

infix fun <X, Y : X, L : List<X>> L.firstIndexOf(item: Y): Int {
    return indexOf(item as X)
}