package it.unibo.coordination.control.impl

import it.unibo.coordination.Promise
import it.unibo.coordination.control.Activity
import java.util.concurrent.Semaphore

class SyncRunner<E, T, R>(activity: Activity<E, T, R>) : FSARunner<E, T, R>(activity) {

    private val resumeSignal = Semaphore(0)

    override fun runBegin(environment: E, continuation: (E, error: Throwable?) -> Unit) {
        safeExecute(continuation) {
            activity.onBegin(environment, controller)
        }
    }

    override fun runStep(data: T, continuation: (E, T, error: Throwable?) -> Unit) {
        safeExecute(continuation) {
            activity.onStep(environment!!, data, controller)
        }
    }

    override fun runEnd(result: R, continuation: (E, T, R, error: Throwable?) -> Unit) {
        safeExecute(continuation) {
            activity.onEnd(environment!!, data!!, result, controller)
        }
    }

    override fun resumeImpl() {
        resumeSignal.release()
    }

    override fun onPauseRealised() {
        resumeSignal.acquire()
    }

    override fun run(environment: E): Promise<R> {
        val result = Promise<R>()
        this.environment = environment
        while (!isOver) {
            runNext {
                if (it != null) {
                    result.completeExceptionally(it)
                }
            }
        }
        result.complete(this.result)
        return result
    }
}