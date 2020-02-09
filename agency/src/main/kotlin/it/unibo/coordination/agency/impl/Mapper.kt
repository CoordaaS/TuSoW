package it.unibo.coordination.agency.impl

import it.unibo.coordination.agency.Behaviour

class Mapper<T, U>(val behaviour: Behaviour<T>, val mapper: (T) -> U) : AbstractBehaviour<U>() {

    override fun onExecute(ctl: Behaviour.Controller<U>) {
        if (executions == 0 || !behaviour.isOver) {
            behaviour(ctl.agent)
        } else {
            ctl.end(mapper(behaviour.result))
        }
    }

    override fun clone(): Behaviour<U> {
        return Mapper(behaviour, mapper)
    }

}