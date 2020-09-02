package it.unibo.coordination.control

import java.time.Duration

class TestPeriodicEventLoop : AbstractTestEventLoop() {
    override fun <E> start(eventLoop: EventLoop<E>) {
        eventLoop.startPeriodic(Duration.ofMillis(200))
    }
}