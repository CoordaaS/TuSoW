import it.unibo.coordination.control.Activity
import it.unibo.coordination.control.runOnBackgroundThread
import kotlin.system.exitProcess

fun main() {
    lateinit var x: Activity.Controller<Int, Int, Int>

    val activity = object : Activity<Int, Int, Int> {
        override fun onBegin(input: Int, controller: Activity.Controller<Int, Int, Int>) {
            println(input)
            controller.`continue`(input + 1)
        }

        override fun onStep(input: Int, lastData: Int, controller: Activity.Controller<Int, Int, Int>) {
            println(lastData)
            if (lastData >= 10) {
                controller.stop(-1)
            } else if (lastData == 5) {
                x = controller
                controller.pause(lastData + 1)
            } else {
                controller.`continue`(lastData + 1)
            }
        }

        override fun onEnd(input: Int, lastData: Int, result: Int, controller: Activity.Controller<Int, Int, Int>) {
            println(result)
        }
    }

    val y = activity.runOnBackgroundThread(0).whenComplete { res, e ->
        if (res != null) {
            println(res)
        }
        if (e !== null) {
            println(e)
        }
    }

    Thread.sleep(2000)
    x.resume()
    print("Result=" + y.get())
    exitProcess(0)
}