package it.unibo.coordination.agency.impl

import it.unibo.coordination.agency.Behaviour

class Mapper<T, U>(val behaviour: Behaviour<T>, val mapper: (T) -> U) : AbstractBehaviour<U>() {

    override fun onExecute(ctl: Behaviour.Controller<U>) {
        behaviour(ctl.agent)
        if (behaviour.isOver) {
            ctl.end(mapper(behaviour.result))
        }
    }

}