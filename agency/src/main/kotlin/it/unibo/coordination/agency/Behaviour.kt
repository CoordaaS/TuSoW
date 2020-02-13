package it.unibo.coordination.agency

import it.unibo.coordination.agency.impl.Mapper
import it.unibo.coordination.agency.impl.Sequence

interface Behaviour<T> {

    interface Controller<T> {

        val agent: Agent.Controller

        fun end(value: T)
    }

    companion object : BehaviourFactory {

        fun <T> of(value: T): Behaviour<T> = valueOf(value)

        fun <T> of(value: () -> T): Behaviour<T> = generate(value)

        fun <I, T> of(input: I, mapper: (I) -> T): Behaviour<T> = generate(input, mapper)
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
    infix fun <U> map(mapper: (T) -> U): Behaviour<U> =
            Mapper(this) { _, x ->
                mapper(x)
            }

    @JvmDefault
    infix fun <U> then(action: (Controller<U>) -> U): Behaviour<U> =
            Mapper(this) { ctl, _ ->
                action(ctl)
            }

    @JvmDefault
    infix fun then(behaviour: Behaviour<T>): Behaviour<List<T>> =
            Sequence(this, behaviour)
}