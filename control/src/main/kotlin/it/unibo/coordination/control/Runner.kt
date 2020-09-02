package it.unibo.coordination.control

import it.unibo.coordination.Engine
import it.unibo.coordination.Engines
import it.unibo.coordination.Promise
import it.unibo.coordination.control.impl.AsyncRunner
import it.unibo.coordination.control.impl.PeriodicRunner
import it.unibo.coordination.control.impl.SyncRunner
import it.unibo.coordination.control.impl.ThreadRunner
import it.unibo.coordination.utils.TimedEngine
import java.time.Duration

interface Runner<E, T, R> {

    val activity: Activity<E, T, R>

    val isOver: Boolean
    val isPaused: Boolean

    fun runBegin(input: E, continuation: (E, error: Throwable?) -> Unit = { _, _ -> Unit })
    fun runStep(data: T, continuation: (E, T, error: Throwable?) -> Unit = { _, _, _ -> Unit })
    fun runEnd(result: R, continuation: (E, T, R, error: Throwable?) -> Unit = { _, _, _, _ -> Unit })

    fun resume()

    fun runNext(continuation: (error: Throwable?) -> Unit = { _ -> Unit })

    fun run(input: E): Promise<R>

    companion object {

        fun <E, T, R> backgroundOf(activity: Activity<E, T, R>): Runner<E, T, R> {
            return ThreadRunner(activity)
        }

        fun <E, T, R> syncOf(activity: Activity<E, T, R>): Runner<E, T, R> {
            return SyncRunner(activity)
        }

        fun <E, T, R> asyncOf(activity: Activity<E, T, R>, engine: Engine = Engines.defaultEngine): Runner<E, T, R> {
            return AsyncRunner(activity, engine)
        }

        fun <E, T, R> periodicOf(period: Duration, activity: Activity<E, T, R>, engine: TimedEngine = Engines.defaultTimedEngine): Runner<E, T, R> {
            return PeriodicRunner(period, activity, engine)
        }
    }
}