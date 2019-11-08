package it.unibo.coordination.linda.core

interface Tuple<T : Tuple<T>> {

    val value: Any

    @JvmDefault
    fun matches(template: Template<T>): Boolean {
        @Suppress("UNCHECKED_CAST")
        return template.matches(this as T)
    }
}