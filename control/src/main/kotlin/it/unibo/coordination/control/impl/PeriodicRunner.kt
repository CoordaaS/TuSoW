package it.unibo.coordination.control.impl

import it.unibo.coordination.Engines
import it.unibo.coordination.control.Activity
import it.unibo.coordination.utils.TimedEngine
import java.time.Duration
import java.util.concurrent.TimeUnit

class PeriodicRunner<E, T, R>(private val period: Duration, activity: Activity<E, T, R>, override val engine: TimedEngine = Engines.defaultTimedEngine)
    : AsyncRunner<E, T, R>(activity, engine) {

    constructor(period: Long, activity: Activity<E, T, R>, engine: TimedEngine = Engines.defaultTimedEngine)
        : this(Duration.ofMillis(period), activity, engine)

    constructor(amount: Long, unit: TimeUnit, activity: Activity<E, T, R>, engine: TimedEngine = Engines.defaultTimedEngine)
            : this(Duration.of(amount, unit.toChronoUnit()), activity, engine)

    override fun schedule(action: () -> Unit) {
        engine.schedule(action, period.toMillis(), TimeUnit.MILLISECONDS)
    }

}