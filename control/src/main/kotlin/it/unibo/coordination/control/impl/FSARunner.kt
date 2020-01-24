package it.unibo.coordination.control.impl

import it.unibo.coordination.control.Activity
import it.unibo.coordination.control.Continuation
import it.unibo.coordination.control.Continuation.*
import it.unibo.coordination.control.Runner
import it.unibo.coordination.control.State
import it.unibo.coordination.control.State.*


abstract class FSARunner(override var activity: Activity) : Runner {

    private var state: State? = State.CREATED
    private var continuation: Continuation? = null

    private fun doStateTransition(whatToDo: Continuation) {
        when (state) {
            State.CREATED -> doStateTransitionFromCreated(whatToDo)
            State.STARTED -> doStateTransitionFromStarted(whatToDo)
            State.RUNNING -> doStateTransitionFromRunning(whatToDo)
            State.PAUSED -> doStateTransitionFromPaused(whatToDo)
            State.STOPPED -> doStateTransitionFromStopped(whatToDo)
        }
    }

    protected fun doStateTransitionFromCreated(whatToDo: Continuation) {
        when (whatToDo) {
            CONTINUE -> {
                state = State.STARTED
                doBegin()
            }
            else -> throw IllegalArgumentException("Unexpected transition: $state -$whatToDo-> ???")
        }
    }

    protected fun doStateTransitionFromStarted(whatToDo: Continuation) {
        doStateTransitionFromRunning(whatToDo)
    }

    protected fun doStateTransitionFromPaused(whatToDo: Continuation) {
        doStateTransitionFromRunning(whatToDo)
    }

    protected fun doStateTransitionFromRunning(whatToDo: Continuation) {
        when (whatToDo) {
            PAUSE -> state = PAUSED
            RESTART -> {
                state = STARTED
                doBegin()
            }
            STOP -> {
                state = STOPPED
                doEnd()
            }
            CONTINUE -> {
                state = RUNNING
                doRun()
            }
        }
    }

    protected fun doStateTransitionFromStopped(whatToDo: Continuation) {
        when (whatToDo) {
            RESTART -> {
                state = STARTED
                doBegin()
            }
            else -> {
                state = null
//                termination.complete(null)
            }
        }
    }


}