package it.unibo.coordination.control

import org.junit.Assert
import java.util.concurrent.Semaphore

class TestSyncActivity : AbstractTestActivity(pause = false) {

    override fun <E, R> run(activity: Activity<E, *, R>, input: E, resultHandler: (R) -> Unit) {
        resultHandler(activity.runOnCurrentThread(input))
    }

    override fun whileAwaitingTermination(pausedSignal: Semaphore, controller: Activity.Controller<String, Int, Long>) {
        Assert.assertThrows(IllegalStateException::class.java) {
            controller.resume()
        }
    }

    override fun onPause(events: MutableList<Any>, controller: Activity.Controller<String, Int, Long>) {
        // do nothing
    }
}