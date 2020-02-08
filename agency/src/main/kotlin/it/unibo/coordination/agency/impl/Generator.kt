package it.unibo.coordination.agency.impl

import it.unibo.coordination.agency.Agent
import it.unibo.coordination.agency.Behaviour

class Generator<T>(val supplier: () -> T) : Behaviour<T> {
    override val result: T
        get() = supplier()

    override fun invoke(ctl: Agent.Controller) = Unit

    override fun onExecute(ctl: Behaviour.Controller<T>) = Unit

}