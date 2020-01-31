package it.unibo.coordination.control.impl

import it.unibo.coordination.Engine
import it.unibo.coordination.Promise
import it.unibo.coordination.control.Activity

abstract class AsyncRunner<E, T, R>(activity: Activity<E, T, R>, val engine: Engine) : FSARunner<E, T, R>(activity) {

    override fun runBegin(environment: E): Promise<T> {
        val promise  = Promise<T>()
        engine.execute {
            activity.onBegin(environment, controller)
            promise.complete(data.orElse(null))
        }
        return promise
    }

    override fun runStep(data: T): Promise<T> {
        val promise  = Promise<T>()
        engine.execute {
            activity.onStep(environment, data, controller)
            promise.complete(data)
        }
        return promise
    }

    override fun runEnd(result: R): Promise<T> {
        val promise  = Promise<T>()
        engine.execute {
            activity.onEnd(environment, data.orElse(null), result, controller)
            promise.complete(data.orElse(null))
        }
        return promise
    }
}