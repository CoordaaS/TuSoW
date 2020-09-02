package it.unibo.coordination.control

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.concurrent.Semaphore

abstract class AbstractTestActivity {

    companion object {
        const val STEPS = 10
    }

    open fun onPause(events: MutableList<Any>, controller: Activity.Controller<String, Int, Long>) {
        events.add("pause")
        println("pause")
    }

    open fun whileAwaitingTermination(pausedSignal: Semaphore, controller: Activity.Controller<String, Int, Long>) {
        pausedSignal.acquire()
        controller.resume()
    }

    abstract fun <E, R> run(activity: Activity<E, *, R>, input: E, resultHandler: (R) -> Unit)

    @Test
    open fun testActivity() {
        val events: MutableList<Any> = mutableListOf()
        var ctl: Activity.Controller<String, Int, Long>? = null
        var result: Long? = null
        val pausedSignal = Semaphore(0)
        val begunSignal = Semaphore(0)
        val terminationSignal = Semaphore(0)

        val activity = object : Activity<String, Int, Long> {
            override fun onBegin(input: String, controller: Activity.Controller<String, Int, Long>) {
                ctl = controller
                begunSignal.release()
                events.add(input)
                println(input)
                controller.`continue`(input.toInt())
            }

            override fun onStep(input: String, lastData: Int, controller: Activity.Controller<String, Int, Long>) {
                events.add(lastData)
                println(lastData)
                if (lastData >= STEPS) {
                    controller.stop(-1L)
                } else if (lastData == STEPS / 2) {
                    onPause(events, controller)
                    controller.pause(lastData + 1)
                    pausedSignal.release()
                } else {
                    controller.`continue`(lastData + 1)
                }
            }

            override fun onEnd(input: String, lastData: Int, result: Long, controller: Activity.Controller<String, Int, Long>) {
                events.add(result)
                println(result)
            }
        }

        run(activity, "0") {
            result = it
            terminationSignal.release()
        }

        begunSignal.acquire()
        whileAwaitingTermination(pausedSignal, ctl!!)

        terminationSignal.acquire()
        assertEquals(-1L, result!!)
        assertEquals(
                listOf("0") + (0..STEPS / 2) + listOf("pause") + ((STEPS / 2 + 1)..10) + listOf(-1L),
                events
        )
    }
}