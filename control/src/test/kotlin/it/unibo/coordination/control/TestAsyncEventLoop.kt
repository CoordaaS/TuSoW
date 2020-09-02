package it.unibo.coordination.control

class TestAsyncEventLoop : AbstractTestEventLoop() {
    override fun <E> start(eventLoop: EventLoop<E>) {
        eventLoop.start()
    }
}