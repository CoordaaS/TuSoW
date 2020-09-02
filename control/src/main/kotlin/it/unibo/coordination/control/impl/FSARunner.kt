package it.unibo.coordination.control.impl

import it.unibo.coordination.control.Activity
import it.unibo.coordination.control.Runner
import it.unibo.coordination.control.impl.FSARunner.Continuation.*
import it.unibo.coordination.control.impl.State.*


abstract class FSARunner<E, T, R>(override val activity: Activity<E, T, R>) : Runner<E, T, R> {

    protected enum class Continuation {
        CONTINUE, PAUSE, RESTART, STOP;
    }

    protected val controller: Activity.Controller<E, T, R> = object : Activity.Controller<E, T, R> {
        override fun stop(result: R) {
            this@FSARunner.result = result
            continuation = STOP
        }

        override fun restart(input: E) {
            this@FSARunner.environment = input
            continuation = RESTART
        }

        override fun pause(data: T) {
            this@FSARunner.data = data
            pause()
        }

        override fun pause() {
            continuation = PAUSE
            onPauseInvoked()
        }

        override fun `continue`(data: T) {
            this@FSARunner.data = data
            `continue`()
        }

        override fun `continue`() {
            continuation = CONTINUE
        }

        override fun resume() {
            this@FSARunner.resume()
        }

    }

    private var state: State? = CREATED
    private var continuation: Continuation = CONTINUE

    protected var environment: E? = null
    protected var data: T? = null
    protected var result: R? = null

    override val isOver: Boolean
        get() = state == null

    override val isPaused: Boolean
        get() = state == PAUSED

    private fun doStateTransition(whatToDo: Continuation, action: (error: Throwable?) -> Unit) {
        val onError = { e: Throwable? ->
            if (e !== null) {
                state = null
            }
            action(e)
        }
        return when (state) {
            CREATED -> doStateTransitionFromCreated(whatToDo, onError)
            STARTED -> doStateTransitionFromStarted(whatToDo, onError)
            RUNNING -> doStateTransitionFromRunning(whatToDo, onError)
            PAUSED -> doStateTransitionFromPaused(whatToDo, onError)
            STOPPED -> doStateTransitionFromStopped(whatToDo, onError)
            else -> throw IllegalStateException("Illegal state: $state")
        }
    }

    private fun doStateTransitionFromCreated(whatToDo: Continuation, action: (error: Throwable?) -> Unit) {
        return when (whatToDo) {
            CONTINUE -> {
                state = STARTED
                runBegin(environment!!) { _, e -> action(e) }
            }
            else -> throw IllegalArgumentException("Unexpected transition: $state -$whatToDo-> ???")
        }
    }

    private fun doStateTransitionFromStarted(whatToDo: Continuation, action: (error: Throwable?) -> Unit) {
        return doStateTransitionFromRunning(whatToDo, action)
    }

    private fun doStateTransitionFromPaused(whatToDo: Continuation, action: (error: Throwable?) -> Unit) {
        return doStateTransitionFromRunning(whatToDo, action)
    }

    private fun doStateTransitionFromRunning(whatToDo: Continuation, action: (error: Throwable?) -> Unit) {
        return when (whatToDo) {
            PAUSE -> {
                state = PAUSED
                var exception: InterruptedException? = null
                try {
                    onPauseRealised()
                } catch (e: InterruptedException) {
                    exception = e
                } finally {
                    action(exception)
                }
            }
            RESTART -> {
                state = STARTED
                runBegin(environment!!) { _, e -> action(e) }
            }
            STOP -> {
                state = STOPPED
                runEnd(result!!) { _, _, _, e -> action(e) }
            }
            CONTINUE -> {
                state = RUNNING
                runStep(data!!) { _, _, e -> action(e) }
            }
        }
    }

    private fun doStateTransitionFromStopped(whatToDo: Continuation, action: (error: Throwable?) -> Unit) {
        return when (whatToDo) {
            RESTART -> {
                state = STARTED
                runBegin(environment!!) { _, e -> action(e) }
            }
            else -> {
                state = null
                action(null)
            }
        }
    }

    override fun resume() {
        if (isPaused) {
            continuation = CONTINUE
            state = RUNNING
            resumeImpl()
        }
    }

    protected abstract fun resumeImpl()

    protected open fun onPauseInvoked() = Unit

    @Throws(InterruptedException::class)
    protected open fun onPauseRealised() = Unit

    override fun runNext(continuation: (error: Throwable?) -> Unit) {
        if (state !== null) {
            return doStateTransition(this.continuation, continuation)
        } else {
            throw IllegalStateException("Cannot run next step in terminated activity")
        }
    }

    protected fun safeExecute(continuation: (environment: E, error: Throwable?) -> Unit,
                              action: () -> Unit) {
        var error: Throwable? = null
        try {
            action()
        } catch (e: Throwable) {
            error = e
        } finally {
            continuation(environment!!, error)
        }
    }

    protected fun safeExecute(continuation: (environment: E, data: T, error: Throwable?) -> Unit,
                              action: () -> Unit) {
        var error: Throwable? = null
        try {
            action()
        } catch (e: Throwable) {
            error = e
        } finally {
            continuation(environment!!, data!!, error)
        }
    }

    protected fun safeExecute(continuation: (environment: E, data: T, result: R, error: Throwable?) -> Unit,
                              action: () -> Unit) {
        var error: Throwable? = null
        try {
            action()
        } catch (e: Throwable) {
            error = e
        } finally {
            continuation(environment!!, data!!, result!!, error)
        }
    }
}