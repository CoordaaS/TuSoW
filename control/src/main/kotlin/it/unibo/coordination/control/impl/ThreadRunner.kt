package it.unibo.coordination.control.impl

import it.unibo.coordination.Promise
import it.unibo.coordination.control.Activity
import java.util.concurrent.Semaphore

class ThreadRunner<E, T, R>(activity: Activity<E, T, R>) : FSARunner<E, T, R>(activity) {

    private val finalResult = Promise<R>()
    private val thread = Thread(this::runImpl)
    private val mutex = Semaphore(0)

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
        mutex.release()
    }

    override fun onPauseRealised() {
        mutex.acquire()
    }

    override fun run(environment: E): Promise<R> {
        this.environment = environment
        thread.start()
        return finalResult
    }

    private fun runImpl() {
        while (!isOver) {
            runNext {
                if (it != null) {
                    finalResult.completeExceptionally(it)
                }
            }
        }
        finalResult.complete(result)
    }
}