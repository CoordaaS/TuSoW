package it.unibo.coordination.agency.impl

import it.unibo.coordination.agency.Behaviour

class Computer<T, U>(private val input: T, private val function: (T) -> U) : AbstractBehaviour<U>() {
    override fun clone(): Behaviour<U> {
        return Computer(input, function)
    }

    override fun onExecute(ctl: Behaviour.Controller<U>) {
        ctl.end(function(input))
    }


}