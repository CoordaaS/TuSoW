package it.unibo.coordination.agency

import it.unibo.coordination.agency.impl.Computer
import it.unibo.coordination.agency.impl.Generator
import it.unibo.coordination.agency.impl.Value

interface BehaviourFactory {
    fun<T> valueOf(value: T): Behaviour<T> = Value(value)

    fun<T> action(supplier: () -> T): Behaviour<T> = Generator(supplier)

    fun<T, U> generate(seed: T, supplier: (T) -> U): Behaviour<U> = Computer(seed, supplier)
}