package it.unibo.coordination.control

import it.unibo.coordination.Promise

interface Runner<E, T, R> {

    val activity: Activity<E, T, R>

    val isOver: Boolean

    fun runBegin(environment: E): Promise<T>
    fun runStep(data: T): Promise<T>
    fun runEnd(result: R): Promise<T>

    fun resume()

    fun runNext(): Promise<*>
}