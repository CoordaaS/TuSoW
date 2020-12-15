package it.unibo.coordination.control

import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.time.Duration

@RunWith(Parameterized::class)
class TestPeriodicEventLoop(val index: Int) : AbstractTestEventLoop() {
    companion object {
        @Parameterized.Parameters
        @JvmStatic
        fun getParams(): Array<Array<Any>> = TestUtils.repetitionParams
    }

    override fun <E> start(eventLoop: EventLoop<E>) {
        eventLoop.startPeriodic(Duration.ofMillis(200))
    }
}