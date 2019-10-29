package it.unibo.coordination.linda.core

interface Tuple {

    val value: Any

    @JvmDefault
    fun matches(template: Template): Boolean {
        return template.matches(this)
    }
}