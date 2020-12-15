package it.unibo.coordination.control

import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class TestBackgroundEventLoop(val index: Int) : AbstractTestEventLoop() {

    companion object {
        @Parameterized.Parameters
        @JvmStatic
        fun getParams(): Array<Array<Any>> = TestUtils.repetitionParams
    }

    override fun <E> start(eventLoop: EventLoop<E>) {
        eventLoop.runOnBackgroundThread()
    }

}