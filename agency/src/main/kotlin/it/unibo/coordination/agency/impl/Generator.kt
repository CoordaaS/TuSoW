package it.unibo.coordination.agency.impl

import it.unibo.coordination.agency.Behaviour

class Generator<T>(val supplier: () -> T) : AbstractBehaviour<T>() {

    override fun onExecute(ctl: Behaviour.Controller<T>) {
        ctl.end(supplier())
    }

    override fun clone(): Behaviour<T> {
        return Generator(supplier)
    }

}