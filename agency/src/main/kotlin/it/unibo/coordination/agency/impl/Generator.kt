package it.unibo.coordination.agency.impl

import it.unibo.coordination.agency.Behaviour

class Generator<T>(val supplier: (Behaviour.Controller<T>) -> T) : AbstractBehaviour<T>() {

    override fun onExecute(ctl: Behaviour.Controller<T>) {
        ctl.end(supplier(ctl))
    }

    override fun clone(): Behaviour<T> {
        return Generator(supplier)
    }

}