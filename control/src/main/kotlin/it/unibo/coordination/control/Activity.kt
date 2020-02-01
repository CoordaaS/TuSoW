package it.unibo.coordination.control

interface Activity<E, T, R> {

    interface Controller<E, T, R> {
        fun stop(result: R)
        fun restart(environment: E)
        fun pause(data: T)
        fun pause()
        fun `continue`(data: T)
        fun `continue`()
        fun resume()
    }

    fun onBegin(environment: E, controller: Controller<E, T, R>)
    fun onStep(environment: E, lastData: T, controller: Controller<E, T, R>)
    fun onEnd(environment: E, lastData: T, result: R, controller: Controller<E, T, R>)
}