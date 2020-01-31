package it.unibo.coordination.control.impl

import it.unibo.coordination.Promise
import it.unibo.coordination.control.Activity
import it.unibo.coordination.control.Runner
import it.unibo.coordination.control.impl.Continuation.*
import it.unibo.coordination.control.impl.State.*
import java.util.*


abstract class FSARunner<E, T, R>(override val activity: Activity<E, T, R>) : Runner<E, T, R> {

    protected val controller: Activity.Controller<E, T, R> = object : Activity.Controller<E, T, R> {
        override fun stop(result: R) {
            this@FSARunner.result =  Optional.ofNullable(result)
            continuation = STOP
        }

        override fun restart(environment: E) {
            this@FSARunner.environment = environment
            continuation = RESTART
        }

        override fun pause(data: T) {
            this@FSARunner.data = Optional.ofNullable(data)
            pause()
        }

        override fun pause() {
            continuation = PAUSE
        }

        override fun `continue`(data: T) {
            this@FSARunner.data = Optional.ofNullable(data)
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

    protected abstract var environment: E
    protected var data: Optional<T> = Optional.empty()
    protected var result: Optional<R> = Optional.empty()

    override val isOver: Boolean
        get() = state == null

    private fun doStateTransition(whatToDo: Continuation): Promise<*> {
        return when (state) {
            CREATED -> doStateTransitionFromCreated(whatToDo)
            STARTED -> doStateTransitionFromStarted(whatToDo)
            RUNNING -> doStateTransitionFromRunning(whatToDo)
            PAUSED -> doStateTransitionFromPaused(whatToDo)
            STOPPED -> doStateTransitionFromStopped(whatToDo)
            else -> throw IllegalStateException()
        }
    }

    protected fun doStateTransitionFromCreated(whatToDo: Continuation): Promise<*> {
        return when (whatToDo) {
            CONTINUE -> {
                state = STARTED
                runBegin(environment)
            }
            else -> throw IllegalArgumentException("Unexpected transition: $state -$whatToDo-> ???")
        }
    }



    protected fun doStateTransitionFromStarted(whatToDo: Continuation): Promise<*> {
        return doStateTransitionFromRunning(whatToDo)
    }

    protected fun doStateTransitionFromPaused(whatToDo: Continuation): Promise<*> {
        return doStateTransitionFromRunning(whatToDo)
    }

    protected fun doStateTransitionFromRunning(whatToDo: Continuation): Promise<*> {
        return when (whatToDo) {
            PAUSE -> {
                state = PAUSED
                Promise.completedFuture(data)
            }
            RESTART -> {
                state = STARTED
                runBegin(environment)
            }
            STOP -> {
                state = STOPPED
                runEnd(result.orElse(null))
            }
            CONTINUE -> {
                state = RUNNING
                runStep(data.orElse(null))
            }
        }
    }

    protected fun doStateTransitionFromStopped(whatToDo: Continuation): Promise<*> {
        return when (whatToDo) {
            RESTART -> {
                state = STARTED
                runBegin(environment)
            }
            else -> {
                state = null
//                termination.complete(null)
                Promise.completedFuture(result)
            }
        }
    }


    override fun resume() {
        if (state == PAUSED) {
            doStateTransition(continuation)
        }
    }

    override fun runNext(): Promise<out Any> {
        if (state !== null) {
            return doStateTransition(continuation)
        } else {
            throw IllegalStateException("Cannot run next step in terminated activity")
        }
    }

}