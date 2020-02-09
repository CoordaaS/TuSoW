package it.unibo.coordination.agency

import it.unibo.coordination.agency.impl.Computer
import it.unibo.coordination.agency.impl.Generator
import it.unibo.coordination.agency.impl.Value

interface BehaviourFactory {
    @JvmDefault
    fun<T> valueOf(value: T): Behaviour<T> = Value(value)

    @JvmDefault
    fun<T> action(supplier: () -> T): Behaviour<T> = Generator(supplier)

    @JvmDefault
    fun<T, U> generate(seed: T, supplier: (T) -> U): Behaviour<U> = Computer(seed, supplier)
}