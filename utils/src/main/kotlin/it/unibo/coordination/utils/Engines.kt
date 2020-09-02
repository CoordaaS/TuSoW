package it.unibo.coordination

import it.unibo.coordination.utils.TimedEngine
import java.util.concurrent.Executors

object Engines {

    @JvmStatic
    val defaultEngine: Engine
        get() = defaultTimedEngine

    @JvmStatic
    val defaultTimedEngine: TimedEngine by lazy {
        Executors.newSingleThreadScheduledExecutor()
    }
}