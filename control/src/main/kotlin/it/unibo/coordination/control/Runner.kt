package it.unibo.coordination.control

import it.unibo.coordination.Promise

interface Runner {

    val activity: Activity

    fun runStep(): Promise<State>
}