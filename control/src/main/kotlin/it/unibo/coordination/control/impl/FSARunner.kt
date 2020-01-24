package it.unibo.coordination.control.impl

import it.unibo.coordination.control.Activity
import it.unibo.coordination.control.Continuation
import it.unibo.coordination.control.Runner
import it.unibo.coordination.control.State

abstract class FSARunner(override var activity: Activity) : Runner {

    private var state: State = State.CREATED
    private var continuation: Continuation? = null

}