package it.unibo.coordination.agency

import it.unibo.coordination.agency.impl.Computer
import it.unibo.coordination.agency.impl.Generator
import it.unibo.coordination.agency.impl.Parallel
import it.unibo.coordination.agency.impl.Value

interface BehaviourFactory {
    @JvmDefault
    fun<T> valueOf(value: T): Behaviour<T> =
            Value(value)

    @JvmDefault
    fun<T> action(supplier: () -> T): Behaviour<T> =
            Generator(supplier)

    @JvmDefault
    fun<T, U> generate(seed: T, supplier: (T) -> U): Behaviour<U> =
            Computer(seed, supplier)

    @JvmDefault
    fun<T> anyOf(behaviour: Behaviour<T>, vararg behaviours: Behaviour<T>): Behaviour<List<T>> =
            Parallel.AllOf(behaviour, *behaviours)

    @JvmDefault
    fun<T> allOf(behaviour: Behaviour<T>, vararg behaviours: Behaviour<T>): Behaviour<List<T>> =
            Parallel.AnyOf(behaviour, *behaviours)
}