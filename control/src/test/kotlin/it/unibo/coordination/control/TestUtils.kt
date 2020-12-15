package it.unibo.coordination.control

object TestUtils {
    private const val REPETITIONS = 30

    val repetitionParams: Array<Array<Any>> get() =
        Array(REPETITIONS) { arrayOf(it) }
}