package it.unibo.coordination.control

interface Activity<E, T, R> {

    interface Controller<E, T, R> {
        fun stop(result: R)
        fun restart(input: E)
        fun pause(data: T)
        fun pause()
        fun `continue`(data: T)
        fun `continue`()
        fun resume()
    }

    fun onBegin(input: E, controller: Controller<E, T, R>)
    fun onStep(input: E, lastData: T, controller: Controller<E, T, R>)
    fun onEnd(input: E, lastData: T, result: R, controller: Controller<E, T, R>)
}