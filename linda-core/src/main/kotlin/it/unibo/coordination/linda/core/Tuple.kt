package it.unibo.coordination.linda.core

interface Tuple<T : Tuple<T>> {

    val value: Any

    @JvmDefault
    fun matches(template: Template<T>): Boolean {
        return template.matches(this as T)
    }
}

//fun <T : Tuple<T>> T.matches(template: Template<T>): Boolean {
//    return template.matches(this)
//}