package it.unibo.coordination.control.impl

import it.unibo.coordination.Promise
import it.unibo.coordination.control.Activity

abstract class SyncRunner<E, T, R>(activity: Activity<E, T, R>) : FSARunner<E, T, R>(activity) {

    override fun runBegin(environment: E): Promise<T> {
        activity.onBegin(environment, controller)
        return Promise.completedFuture(data.orElse(null))
    }

    override fun runStep(data: T): Promise<T> {
        activity.onStep(environment, data, controller)
        return Promise.completedFuture(data)
    }

    override fun runEnd(result: R): Promise<T> {
        activity.onEnd(environment, data.orElse(null), result, controller)
        return Promise.completedFuture(data.orElse(null))
    }
}