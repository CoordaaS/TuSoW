package it.unibo.coordination.control

import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.util.concurrent.Semaphore

@RunWith(Parameterized::class)
class TestBackgroundActivity(val index: Int) : AbstractTestActivity() {

    companion object {
        @Parameterized.Parameters
        @JvmStatic
        fun getParams(): Array<Array<Any>> = TestUtils.repetitionParams
    }

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