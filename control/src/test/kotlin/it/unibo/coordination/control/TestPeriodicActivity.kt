package it.unibo.coordination.control

import it.unibo.coordination.Engines
import org.junit.Assert
import org.junit.Test
import java.time.Duration
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit

class TestPeriodicActivity : AbstractTestActivity() {

    companion object {
        const val PERIOD: Long = 200
        const val PAUSE: Long = 500
        const val TOTAL: Long = PERIOD * (STEPS + 2) + PAUSE
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
        val initTime = System.currentTimeMillis()
        super.testActivity()
        val endTime = System.currentTimeMillis()
        val elapsedTime = endTime - initTime
        Assert.assertTrue(
                "The activity execution should have required at least $TOTAL ms, while it required only $elapsedTime ms",
                elapsedTime >= TOTAL
        )
    }
}