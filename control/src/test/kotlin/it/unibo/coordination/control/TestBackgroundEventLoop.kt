package it.unibo.coordination.control

class TestBackgroundEventLoop : AbstractTestEventLoop() {
    override fun <E> start(eventLoop: EventLoop<E>) {
        eventLoop.runOnBackgroundThread()
    }
}