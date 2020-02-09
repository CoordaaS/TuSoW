import it.unibo.coordination.agency.Agent
import it.unibo.coordination.control.runInCurrentThread

fun main() {
    val agent = Agent("Alice") {
        setup {
            behaviour {
                action { println(1); 1 }
                        .map { value -> (value * 2).also { println(it) } }
                        .map { println(it + 1) }
            }

            behaviour {
                action { "a".also { println(it) } }
                        .map { value -> (value + value).also { println(it) } }
                        .map { println(it + "b") }
            }
        }
    }

    agent.runInCurrentThread()
}