package it.unibo.coordination.agency

import it.unibo.coordination.agency.impl.AgentImpl
import it.unibo.coordination.control.Activity

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

        @JvmDefault
        operator fun plusAssign(behaviour: Behaviour<*>) {
            addBehaviours(behaviour)
        }

        @JvmDefault
        operator fun minusAssign(behaviour: Behaviour<*>) {
            removeBehaviours(behaviour)
        }

        fun <T> behaviour(context: BehaviourFactory.() -> Behaviour<T>): Behaviour<T> =
                Behaviour.context().also { addBehaviours(it) }
    }

    val behaviours: List<Behaviour<*>>

    val id: AgentID

    fun setup(initializer: Controller.() -> Unit)

    fun tearDown(cleaner: Agent.() -> Unit)

    companion object {

        @JvmStatic
        fun create(name: String): Agent = AgentImpl(name)

        @JvmStatic
        fun create(name: String, agentConfiguration: Agent.() -> Unit): Agent = AgentImpl(name).also(agentConfiguration)

        @JvmStatic
        operator fun invoke(name: String): Agent = create(name)

        @JvmStatic
        operator fun invoke(name: String, agentConfiguration: Agent.() -> Unit): Agent = create(name, agentConfiguration)
    }
}