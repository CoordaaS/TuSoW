package it.unibo.presentation

data class TypeToken<T>(val type: Class<T>, val genericTypes: List<Class<*>>) {
    constructor(type: Class<T>, vararg genericTypes: Class<*>) : this(type, genericTypes.toList())

    val arity: Int get() = genericTypes.size

    fun isAssignableBy(other: TypeToken<*>): Boolean {
        return arity == other.arity
                && type.isAssignableFrom(other.type)
                && (0 until arity).all { genericTypes[it].isAssignableFrom(other.genericTypes[it])  }
    }

    private fun getName(naming: (Class<*>) -> String): String =
            "${naming(type)}${if (arity > 0) genericTypes.map { naming(it) }.joinToString(", ", "<", ">") else ""}"

    val name: String get() = getName(Class<*>::getName)

    val simpleName: String get() = getName(Class<*>::getSimpleName)
}

fun <T> Class<T>.toTypeToken(vararg genericTypes: Class<*>): TypeToken<T> = TypeToken(this, *genericTypes)