package it.unibo.coordination.linda.core.traits

import it.unibo.coordination.Promise

interface OperationalPrimitives {
    fun getSize(): Promise<Int>
}