package it.unibo.coordination.control.impl

import it.unibo.coordination.Engine
import it.unibo.coordination.Promise
import it.unibo.coordination.control.Activity
import java.util.concurrent.CompletableFuture

open class AsyncRunner<E, T, R>(activity: Activity<E, T, R>, protected open val engine: Engine) : FSARunner<E, T, R>(activity) {

    private val finalResult = Promise<R>()

    protected open fun schedule(action: () -> Unit) {
        engine.execute(action)
    }

    private fun scheduleAndThen(continuation: (environment: E, error: Throwable?) -> Unit, action: () -> Unit) {
        schedule {
            safeExecute(continuation, action)
        }
    }

    private fun scheduleAndThen(continuation: (environment: E, data: T, error: Throwable?) -> Unit, action: () -> Unit) {
        schedule {
            safeExecute(continuation, action)
        }
    }

    private fun scheduleAndThen(continuation: (environment: E, data: T, result: R, error: Throwable?) -> Unit,
                                action: () -> Unit) {
        schedule {
            safeExecute(continuation, action)
        }
    }

    override fun runBegin(environment: E, continuation: (E, error: Throwable?) -> Unit) {
        scheduleAndThen(continuation) {
            activity.onBegin(environment, controller)
        }
    }

    override fun runStep(data: T, continuation: (E, T, error: Throwable?) -> Unit) {
        scheduleAndThen(continuation) {
            activity.onStep(environment!!, data, controller)
        }
    }

    override fun runEnd(result: R, continuation: (E, T, R, error: Throwable?) -> Unit) {
        scheduleAndThen(continuation) {
            activity.onEnd(environment!!, data!!, result, controller)
        }
    }

    private fun runImpl(result: CompletableFuture<R>) {
        if (isOver) {
            result.complete(this.result)
        } else if (!isPaused) {
            runNext { e ->
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

    override fun run(environment: E): Promise<R> {
        this.environment = environment
        runImpl(finalResult)
        return finalResult
    }
}