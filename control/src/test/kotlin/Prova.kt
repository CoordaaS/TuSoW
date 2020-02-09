import it.unibo.coordination.Engines
import it.unibo.coordination.control.Activity
import it.unibo.coordination.control.run

fun main() {
    lateinit var x: Activity.Controller<Int, Int, Int>

    val activity = object : Activity<Int, Int, Int> {
        override fun onBegin(environment: Int, controller: Activity.Controller<Int, Int, Int>) {
            println(environment)
            controller.`continue`(environment + 1)
        }

        override fun onStep(environment: Int, lastData: Int, controller: Activity.Controller<Int, Int, Int>) {
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

        override fun onEnd(environment: Int, lastData: Int, result: Int, controller: Activity.Controller<Int, Int, Int>) {
            println(result)
        }
    }

    val y = Engines.defaultTimedEngine.run(0, activity).whenComplete { res, e ->
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
}