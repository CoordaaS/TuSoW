package it.unibo.coordination.utils

infix fun <X, Y : X, L : List<X>> L.indexOf(item: Y): Int {
    return indexOf(item as X)
}