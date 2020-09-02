import it.unibo.coordination.agency.Agent
import it.unibo.coordination.control.runOnBackgroundThread
import kotlin.system.exitProcess

fun main() {
    val agent = Agent("Alice") {
        setup {

            behaviour {
                anyOf(
                        valueOf(1).map { it + 2 }.map { it + 3 },
                        valueOf(1)
                ) map {
                    println("Result of anyOf: $it")
                } then stopAgent()

            }

        }
    }

    agent.runOnBackgroundThread().get()
}