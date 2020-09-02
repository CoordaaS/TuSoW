package it.unibo.coordination.control

import it.unibo.coordination.Engines
import java.util.concurrent.Semaphore

class TestAsyncActivity : AbstractTestActivity() {

    private val engine = Engines.defaultEngine

    override fun <E, R> run(activity: Activity<E, *, R>, input: E, resultHandler: (R) -> Unit) {
        engine.run(activity, input).whenComplete { t, _ ->
            resultHandler(t)
        }
    }

    override fun whileAwaitingTermination(pausedSignal: Semaphore, controller: Activity.Controller<String, Int, Long>) {
        pausedSignal.acquire()
        engine.submit {
            controller.resume()
        }
    }
}