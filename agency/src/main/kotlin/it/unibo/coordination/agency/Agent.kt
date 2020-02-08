package it.unibo.coordination.agency

import it.unibo.coordination.agency.impl.AgentImpl
import it.unibo.coordination.control.Activity
import java.util.*

interface Agent : Activity<Unit, Unit, Unit> {

    interface Controller : Activity.Controller<Unit, Unit, Unit> {
        @JvmDefault
        fun stop() {
            stop(Unit)
        }

        @JvmDefault
        fun restart() {
            restart(Unit)
        }

        @JvmDefault
        override fun pause() {
            pause(Unit)
        }

        @JvmDefault
        override fun `continue`() {
            `continue`(Unit)
        }

        fun addBehaviours(behaviour: Behaviour<*>, vararg behaviours: Behaviour<*>)
        fun removeBehaviours(behaviour: Behaviour<*>, vararg behaviours: Behaviour<*>)
    }

    val behaviours: List<Behaviour<*>>

    val id: AgentID

    fun setup(initializer: Agent.() -> Unit)

    fun tearDown(cleaner: Agent.() -> Unit)

    fun addBehaviours(behaviour: Behaviour<*>, vararg behaviours: Behaviour<*>)
    fun removeBehaviours(behaviour: Behaviour<*>, vararg behaviours: Behaviour<*>)

    @JvmDefault
    operator fun plusAssign(behaviour: Behaviour<*>) {
        addBehaviours(behaviour)
    }

    @JvmDefault
    operator fun minusAssign(behaviour: Behaviour<*>) {
        removeBehaviours(behaviour)
    }

    companion object {

        @JvmStatic
        fun create(name: String): Agent = AgentImpl(name)

        @JvmStatic
        operator fun invoke(name: String): Agent = create(name)
    }
}