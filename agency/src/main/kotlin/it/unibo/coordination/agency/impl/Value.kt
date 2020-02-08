package it.unibo.coordination.agency.impl

import it.unibo.coordination.agency.Agent
import it.unibo.coordination.agency.Behaviour

class Value<T>(override val result: T) : Behaviour<T> {

    override fun invoke(ctl: Agent.Controller) = Unit

    override fun onExecute(ctl: Behaviour.Controller<T>) = Unit

}