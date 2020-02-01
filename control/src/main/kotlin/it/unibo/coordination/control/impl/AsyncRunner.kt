package it.unibo.coordination.control.impl

import it.unibo.coordination.Engine
import it.unibo.coordination.Promise
import it.unibo.coordination.control.Activity
import java.util.*
import java.util.concurrent.CompletableFuture

class AsyncRunner<E, T, R>(activity: Activity<E, T, R>, private val engine: Engine) : FSARunner<E, T, R>(activity) {

    override fun runBegin(environment: E): Promise<T> {
        val promise  = Promise<T>()
        engine.execute {
            try {
                activity.onBegin(environment, controller)
                promise.complete(data.orElse(null))
            } catch (t: Throwable) {
                promise.completeExceptionally(t)
            }
        }
        return promise
    }

    override fun runStep(data: T): Promise<T> {
        val promise  = Promise<T>()
        engine.execute {
            try {
                activity.onStep(environment.orElse(null), data, controller)
                promise.complete(data)
            } catch (t: Throwable) {
                promise.completeExceptionally(t)
            }
        }
        return promise
    }

    override fun runEnd(result: R): Promise<T> {
        val promise  = Promise<T>()
        engine.execute {
            try {
                activity.onEnd(environment.orElse(null), data.orElse(null), result, controller)
                promise.complete(data.orElse(null))
            } catch (t: Throwable) {
                promise.completeExceptionally(t)
            }
        }
        return promise
    }

    private fun runImpl(result: CompletableFuture<R>) {
        if (isOver) {
            result.complete(this.result.get())
        } else {
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
        this.environment = Optional.ofNullable(environment)
        runImpl(finalResult)
        return finalResult
    }
}