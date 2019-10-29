package it.unibo.coordination

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

object Engines {

    @JvmStatic
    val defaultEngine: ExecutorService by lazy {
        Executors.newSingleThreadExecutor()
    }
}