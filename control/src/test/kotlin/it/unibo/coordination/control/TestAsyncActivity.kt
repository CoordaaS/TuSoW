package it.unibo.coordination.control

import it.unibo.coordination.Engines
import it.unibo.coordination.control.TestUtils.repetitionParams
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.util.concurrent.Semaphore

@RunWith(Parameterized::class)
class TestAsyncActivity(val index: Int) : AbstractTestActivity() {

    companion object {
        @Parameterized.Parameters
        @JvmStatic
        fun getParams(): Array<Array<Any>> = repetitionParams
    }

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