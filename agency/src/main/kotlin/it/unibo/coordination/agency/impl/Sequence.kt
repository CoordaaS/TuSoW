package it.unibo.coordination.agency.impl

import it.unibo.coordination.agency.Behaviour
import java.util.*

class Sequence<T>(private vararg val behaviours: Behaviour<T>
) : AbstractBehaviour<List<T>>() {

    init {
        require(behaviours.isNotEmpty()) {
            "A sequential behaviour must contain at least one sub-behaviour"
        }
    }

    private val queue: Deque<Behaviour<T>> = LinkedList(behaviours.toList())

    private val results: MutableList<T> = LinkedList()

    override val isPaused: Boolean
        get() = queue.peek().let { it !== null && it.isPaused }

    override fun onExecute(ctl: Behaviour.Controller<List<T>>) {
        val behaviour = queue.poll()
        try {
            behaviour?.invoke(ctl.agent)
        } finally {
            if (behaviour !== null) {
                if (behaviour.isOver) {
                    results.add(behaviour.result)
                } else {
                    queue.addFirst(behaviour)
                }
            } else {
                ctl.end(results)
            }
        }
    }

    override fun clone(): Behaviour<List<T>> {
        return Sequence(*behaviours)
    }


}