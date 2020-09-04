package it.unibo.coordination.control

import org.junit.Ignore
import java.util.concurrent.Semaphore

@Ignore
class TestBackgroundActivity : AbstractTestActivity() {

    override fun <E, R> run(activity: Activity<E, *, R>, input: E, resultHandler: (R) -> Unit) {
        activity.runOnBackgroundThread(input).whenComplete { t, _ ->
            resultHandler(t)
        }
    }

    override fun whileAwaitingTermination(pausedSignal: Semaphore, controller: Activity.Controller<String, Int, Long>) {
        pausedSignal.acquire()
        controller.resume()
    }
}