package it.unibo.coordination.control.impl

import it.unibo.coordination.Engine
import it.unibo.coordination.Promise
import it.unibo.coordination.control.Activity
import java.util.concurrent.CompletableFuture

open class AsyncRunner<E, T, R>(activity: Activity<E, T, R>, protected open val engine: Engine) : FSARunner<E, T, R>(activity) {

    override fun runBegin(environment: E): Promise<T> = scheduleWithPromise {
        activity.onBegin(environment, controller)
        it.complete(data)
    }

    protected open fun schedule(action: () -> Unit) {
        engine.execute(action)
    }

    protected fun <X> scheduleWithPromise(action: (Promise<X>) -> Unit): Promise<X> {
        val promise  = Promise<X>()
        schedule {
            try {
                action(promise)
            } catch (e: Throwable) {
                e.printStackTrace()
                promise.completeExceptionally(e)
            }
        }
        return promise
    }

    override fun runStep(data: T): Promise<T> = scheduleWithPromise {
        activity.onStep(environment!!, data, controller)
        it.complete(data)
    }

    override fun runEnd(result: R): Promise<T> = scheduleWithPromise {
        activity.onEnd(environment!!, data!!, result, controller)
        it.complete(data!!)
    }

    private fun runImpl(result: CompletableFuture<R>) {
        if (isOver) {
            result.complete(this.result)
        } else if (!isPaused) {
            runNext().whenComplete { _, e ->
                if (e !== null) {
                    result.completeExceptionally(e.cause)
                } else {
                    runImpl(result)
                }
            }
        }
    }

    override fun resumeImpl() {
        runImpl(finalResult)
    }

    private val finalResult = Promise<R>()

    override fun run(environment: E): Promise<R> {
        this.environment = environment
        runImpl(finalResult)
        return finalResult
    }
}