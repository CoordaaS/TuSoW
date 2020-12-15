package it.unibo.coordination.control

import it.unibo.coordination.Engines
import it.unibo.coordination.testing.assertLastsAtLeast
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.time.Duration
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit

@RunWith(Parameterized::class)
class TestPeriodicActivity(val index: Int) : AbstractTestActivity() {

    companion object {
        const val PERIOD: Long = 200

        const val PAUSE: Long = 500

        val TOTAL: Duration = Duration.ofMillis(PERIOD * (STEPS + 2) + PAUSE)

        @Parameterized.Parameters
        @JvmStatic
        fun getParams(): Array<Array<Any>> = TestUtils.repetitionParams
    }

    private val timedEngine = Engines.defaultTimedEngine

    override fun <E, R> run(activity: Activity<E, *, R>, input: E, resultHandler: (R) -> Unit) {
        timedEngine.run(activity, Duration.ofMillis(PERIOD), input).whenComplete { t, _ ->
            resultHandler(t)
        }
    }

    override fun whileAwaitingTermination(pausedSignal: Semaphore, controller: Activity.Controller<String, Int, Long>) {
        pausedSignal.acquire()
        timedEngine.schedule({ controller.resume() }, PAUSE, TimeUnit.MILLISECONDS)
    }

    @Test
    override fun testActivity() {
        assertLastsAtLeast(TOTAL) {
            super.testActivity()
        }
    }
}