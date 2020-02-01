package it.unibo.coordination.control.impl

import it.unibo.coordination.Promise
import it.unibo.coordination.control.Activity
import java.util.*

class SyncRunner<E, T, R>(activity: Activity<E, T, R>) : FSARunner<E, T, R>(activity) {

    override fun runBegin(environment: E): Promise<T> {
        activity.onBegin(environment, controller)
        return Promise.completedFuture(data.orElse(null))
    }

    override fun runStep(data: T): Promise<T> {
        activity.onStep(environment.orElse(null), data, controller)
        return Promise.completedFuture(data)
    }

    override fun runEnd(result: R): Promise<T> {
        activity.onEnd(environment.orElse(null), data.orElse(null), result, controller)
        return Promise.completedFuture(data.orElse(null))
    }

    override fun resumeImpl() {
        throw IllegalStateException("Pausing an activity run by a ")
    }

    override fun run(environment: E): Promise<R> {
        val result = Promise<R>()
        this.environment = Optional.ofNullable(environment)
        while (!isOver) {
            val temp = runNext()
            if (temp.isCompletedExceptionally) {
                result.whenComplete { _, e ->
                    result.completeExceptionally(e)
                }
                return result
            }
        }
        result.complete(this.result.get())
        return result
    }
}