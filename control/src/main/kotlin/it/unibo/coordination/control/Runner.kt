package it.unibo.coordination.control

import it.unibo.coordination.Engine
import it.unibo.coordination.Engines
import it.unibo.coordination.Promise
import it.unibo.coordination.control.impl.AsyncRunner
import it.unibo.coordination.control.impl.PeriodicRunner
import it.unibo.coordination.control.impl.SyncRunner
import it.unibo.coordination.utils.TimedEngine
import java.time.Duration

interface Runner<E, T, R> {

    val activity: Activity<E, T, R>

    val isOver: Boolean
    val isPaused: Boolean

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

        fun<E, T, R> asyncOf(activity: Activity<E, T, R>, engine: Engine = Engines.defaultEngine): Runner<E, T, R> {
            return AsyncRunner(activity, engine)
        }

        fun<E, T, R> periodicOf(period: Duration, activity: Activity<E, T, R>, engine: TimedEngine = Engines.defaultTimedEngine): Runner<E, T, R> {
            return PeriodicRunner(period, activity, engine)
        }
    }
}

fun<E, R> Engine.run(environment: E, activity: Activity<E, *, R>): Promise<R> {
    return Runner.asyncOf(activity, this).run(environment)
}

fun<R> Engine.run(activity: Activity<Unit, *, R>): Promise<R> {
    return Runner.asyncOf(activity, this).run(Unit)
}

fun<E, R> TimedEngine.run(period: Duration, environment: E, activity: Activity<E, *, R>): Promise<R> {
    return Runner.periodicOf(period, activity, this).run(environment)
}

fun<R> TimedEngine.run(period: Duration, activity: Activity<Unit, *, R>): Promise<R> {
    return Runner.periodicOf(period, activity, this).run(Unit)
}

fun <E, T, R> Activity<E, T, R>.runInCurrentThread(environment: E): R {
    return Runner.syncOf(this).run(environment).get()
}

fun <R> Activity<Unit, *, R>.runInCurrentThread(): R {
    return Runner.syncOf(this).run(Unit).get()
}