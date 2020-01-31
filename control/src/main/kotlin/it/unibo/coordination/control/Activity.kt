package it.unibo.coordination.control

interface Activity {

    interface Controller {
        fun stop();
        fun restart();
        fun pause();
        fun `continue`();
    }

    fun onBegin()
    fun onStep()
    fun onEnd()

    fun onError(e: Exception): Continuation
}