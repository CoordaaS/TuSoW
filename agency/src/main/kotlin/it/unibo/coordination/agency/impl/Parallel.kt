package it.unibo.coordination.agency.impl

import it.unibo.coordination.agency.Behaviour
import java.util.*

sealed class Parallel<T>(protected val shortCircuit: Boolean = false,
                         protected vararg val behaviours: Behaviour<T>
) : AbstractBehaviour<List<T>>() {

    init {
        require(behaviours.isNotEmpty()) {
            "A Parallel behaviour must contain at least one sub-behaviour"
        }
    }

    protected val nonTerminated: Queue<Behaviour<T>> = LinkedList(behaviours.toList())

    private val results: MutableList<T> = LinkedList()

    override fun onExecute(ctl: Behaviour.Controller<List<T>>) {
        val skipped: Queue<Behaviour<T>> = LinkedList()
        var behaviour = nonTerminated.poll()
        try {
            while (behaviour != null && behaviour.isPaused) {
                skipped.add(behaviour)
                behaviour = nonTerminated.poll()
            }
            if (behaviour != null) {
                behaviour(ctl.agent)
            } else {
                ctl.agent.pause()
            }
        } finally {
            if (behaviour != null) {
                if (!behaviour.isOver) {
                    nonTerminated.add(behaviour)
                } else {
                    results.add(behaviour.result)
                }
            }
            nonTerminated.addAll(skipped)
        }
    }

    class AnyOf<T>(vararg behaviours: Behaviour<T>) : Parallel<T>(true, *behaviours) {
        override fun clone(): Behaviour<List<T>> {
            return AnyOf(*behaviours)
        }
    }

    class AllOf<T>(vararg behaviours: Behaviour<T>) : Parallel<T>(false, *behaviours) {
        override fun clone(): Behaviour<List<T>> {
            return AllOf(*behaviours)
        }
    }
}