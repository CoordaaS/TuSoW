package it.unibo.coordination.agency.impl

import it.unibo.coordination.agency.Behaviour
import java.util.*

sealed class Parallel<T>(protected val any: Boolean = false,
                         protected vararg val behaviours: Behaviour<T>
) : AbstractBehaviour<List<T>>() {

    init {
        require(behaviours.isNotEmpty()) {
            "A Parallel behaviour must contain at least one sub-behaviour"
        }
    }

    protected var index: Int = 0

    private val results = LinkedList<T>()

    override fun onExecute(ctl: Behaviour.Controller<List<T>>) {
        behaviours[index].let {
            it(ctl.agent)
            if (it.isOver) {
                index += 1
                results += it.result
                if (any || index == behaviours.size) {
                    ctl.end(results)
                }
            }
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