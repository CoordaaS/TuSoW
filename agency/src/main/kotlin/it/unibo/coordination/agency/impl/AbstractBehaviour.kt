package it.unibo.coordination.agency.impl

import it.unibo.coordination.agency.Agent
import it.unibo.coordination.agency.Behaviour

abstract class AbstractBehaviour<T> : Behaviour<T> {

    private var resultCache: T? = null

    override val result: T
        get() = resultCache ?: throw IllegalStateException("Missing result")

    private var over: Boolean = false

    override val isOver: Boolean
        get() = over

    private fun controller(ctl: Agent.Controller): Behaviour.Controller<T> =
            object : Behaviour.Controller<T> {
                override val agent: Agent.Controller
                    get() = ctl

                override fun end(value: T) {
                    resultCache = value
                    over = true
                }
            }

    override fun invoke(ctl: Agent.Controller) {
        if (!over) {
            onExecute(controller(ctl))
        } else {
            throw IllegalStateException("Cannot invoke a behaviour which is over")
        }
    }

    override fun clone(): Behaviour<T> {
        TODO("Subclasses of ${AbstractBehaviour::class.java.name} must override the clone() method")
    }
}