package it.unibo.coordination.agency

import it.unibo.coordination.agency.impl.Mapper

interface Behaviour<T> {

    interface Controller<T> {

        val agent: Agent.Controller

        fun end(value: T)
    }

    companion object : BehaviourFactory {

        fun<T> of(value: T): Behaviour<T> = valueOf(value)
    }

    @JvmDefault
    val isPaused: Boolean
        get() = false

    @JvmDefault
    val isOver: Boolean
        get() = true

    val result: T

    operator fun invoke(ctl: Agent.Controller)

    fun onExecute(ctl: Controller<T>)

    @JvmDefault
    fun clone(): Behaviour<T> = this

    @JvmDefault
    fun<U> then(mapper: (T) -> U): Behaviour<U> = Mapper(this, mapper)
}