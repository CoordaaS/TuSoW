package it.unibo.coordination.control.impl

import it.unibo.coordination.Promise
import it.unibo.coordination.control.Activity

class SyncRunner<E, T, R>(activity: Activity<E, T, R>) : FSARunner<E, T, R>(activity) {

    override fun runBegin(environment: E): Promise<T> {
        activity.onBegin(environment, controller)
        return Promise.completedFuture(data)
    }

    override fun runStep(data: T): Promise<T> {
        activity.onStep(environment!!, data, controller)
        return Promise.completedFuture(data)
    }

    override fun runEnd(result: R): Promise<T> {
        activity.onEnd(environment!!, data!!, result, controller)
        return Promise.completedFuture(data!!)
    }

    override fun resumeImpl() {
        throw IllegalStateException("Resuming an activity run by a ${SyncRunner::class.java.name} is currently not supported")
    }

    override fun onPause() {
        throw IllegalStateException("Pausing an activity run by a ${SyncRunner::class.java.name} is currently not supported")
    }

    override fun run(environment: E): Promise<R> {
        val result = Promise<R>()
        this.environment = environment
        while (!isOver) {
            val temp = runNext()
            if (temp.isCompletedExceptionally) {
                result.whenComplete { _, e ->
                    result.completeExceptionally(e)
                }
                return result
            }
        }
        result.complete(this.result)
        return result
    }
}