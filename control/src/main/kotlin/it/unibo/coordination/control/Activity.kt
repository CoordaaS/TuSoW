package it.unibo.coordination.control

interface Activity {
    fun onBegin()
    fun onStep()
    fun onEnd()

    fun onError(e: Exception): Continuation
}