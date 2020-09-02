package it.unibo.coordination.control

import it.unibo.coordination.Engines
import it.unibo.coordination.testing.assertLastsAtLeast
import java.time.Duration
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit

class TestSyncActivity : AbstractTestActivity() {

    companion object {
        val PAUSE = Duration.ofSeconds(1)
    }

    override fun testActivity() {
        assertLastsAtLeast(PAUSE) {
            super.testActivity()
        }
    }

    override fun <E, R> run(activity: Activity<E, *, R>, input: E, resultHandler: (R) -> Unit) {
        resultHandler(activity.runOnCurrentThread(input))
    }

    override fun whileAwaitingTermination(pausedSignal: Semaphore, controller: Activity.Controller<String, Int, Long>) {
        // do nothing
    }

    override fun onPause(events: MutableList<Any>, controller: Activity.Controller<String, Int, Long>) {
        super.onPause(events, controller)
        Engines.defaultTimedEngine.schedule({ controller.resume() }, PAUSE.toMillis(), TimeUnit.MILLISECONDS)
    }
}