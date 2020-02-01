package it.unibo.coordination.control

import it.unibo.coordination.Engine
import it.unibo.coordination.Promise
import it.unibo.coordination.control.impl.AsyncRunner
import it.unibo.coordination.control.impl.SyncRunner

interface Runner<E, T, R> {

    val activity: Activity<E, T, R>

    val isOver: Boolean

    fun runBegin(environment: E): Promise<T>
    fun runStep(data: T): Promise<T>
    fun runEnd(result: R): Promise<T>

    fun resume()

    fun runNext(): Promise<*>

    fun run(environment: E): Promise<R>

    companion object {
        fun<E, T, R> syncOf(activity: Activity<E, T, R>): Runner<E, T, R> {
            return SyncRunner(activity)
        }

        fun<E, T, R> asyncOf(activity: Activity<E, T, R>, engine: Engine): Runner<E, T, R> {
            return AsyncRunner(activity, engine)
        }
    }
}

fun<E, T, R> Engine.run(environment: E, activity: Activity<E, T, R>): Promise<R> {
    return Runner.asyncOf(activity, this).run(environment)
}

fun <E, T, R> Activity<E, T, R>.run(environment: E): R {
    return Runner.syncOf(this).run(environment).get()
}