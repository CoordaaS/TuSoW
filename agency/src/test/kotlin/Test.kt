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

            behaviour {
                allOf(
                        valueOf(1).map { it + 2 }.map { it + 3 },
                        valueOf(1).map { it * 2 }.map { it * 3 }
                ).map { println("Result of allOf: $it") }
            }

            behaviour {
                anyOf(
                        valueOf(1).map { it + 2 }.map { it + 3 },
                        valueOf(1)
                ).map { println("Result of anyOf: $it") }
            }
        }
    }

    agent.runInCurrentThread()
}