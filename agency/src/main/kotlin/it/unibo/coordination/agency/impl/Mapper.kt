package it.unibo.coordination.agency.impl

import it.unibo.coordination.agency.Behaviour

class Mapper<T, U>(val behaviour: Behaviour<T>, val mapper: (Behaviour.Controller<U>, T) -> U) : AbstractBehaviour<U>() {

    override val isPaused: Boolean
        get() = (executions == 0 || !behaviour.isOver) && behaviour.isPaused

    override fun onExecute(ctl: Behaviour.Controller<U>) {
        if (executions == 0 || !behaviour.isOver) {
            behaviour(ctl.agent)
        } else {
            ctl.end(mapper(ctl, behaviour.result))
        }
    }

    override fun clone(): Behaviour<U> {
        return Mapper(behaviour, mapper)
    }

}