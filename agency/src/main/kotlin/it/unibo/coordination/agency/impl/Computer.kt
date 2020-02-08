package it.unibo.coordination.agency.impl

import it.unibo.coordination.agency.Agent
import it.unibo.coordination.agency.Behaviour

class Computer<T, U>(private val input: T, val function: (T) -> U) : Behaviour<U> {
    override val result: U
        get() = function(input)

    override fun invoke(ctl: Agent.Controller) = Unit

    override fun onExecute(ctl: Behaviour.Controller<U>) = Unit

}