package it.unibo.coordination.control

import org.junit.Assert
import org.junit.Test
import java.util.concurrent.Semaphore

abstract class AbstractTestEventLoop {

    companion object {
        val EVENTS = (1..5)
        val RESULTS = (1..5).map { it * 2 }
    }

    abstract fun <E> start(eventLoop: EventLoop<E>)

    @Test
    fun testEventLoop() {
        val handledEvents = mutableListOf<Int>()
        val endSignal = Semaphore(0)

        val eventLoop = EventLoop.of<Int> {
            handledEvents.add(it * 2)
            if (it == EVENTS.last) {
                endSignal.release()
            }
        }

        EVENTS.forEach {
            eventLoop.schedule(it)
        }

        start(eventLoop)

        endSignal.acquire()
        Assert.assertEquals(
                RESULTS,
                handledEvents
        )
    }

}