package it.unibo.coordination.linda.cli

import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import it.unibo.coordination.linda.cli.TupleSpaceTypes.LOGIC
import it.unibo.coordination.linda.cli.TupleSpaceTypes.TEXT
import it.unibo.coordination.linda.core.Match
import it.unibo.coordination.linda.logic.LogicSpace
import it.unibo.coordination.linda.string.StringSpace
import java.util.concurrent.CompletableFuture

class ReadCommand : AbstractTupleSpaceCommand(name="read") {

    val template: String by argument("TEMPLATE")
    val bulk: Boolean by option("-b", "--bulk").flag(default = false)
    val predicative: Boolean by option("-p", "--predicative").flag(default = false)

    protected fun<T, TT, K, V, M : Match<T, TT, K, V>> CompletableFuture<M>.onReadCompletion(): Unit {
        onCompletion {
            println(if (isMatching()) "Success!" else "Failure!")
            if (isMatching()) {
                println("Success!")
                println("\tResult: ${getTuple()}")
                toMap().let {
                    if (it.isNotEmpty()) {
                        println("\tWhere:")
                        it.forEach { k, v ->
                            println("\t\t$k = $v")
                        }
                    }
                }
            } else {
                println("Failure.")
            }
        }
    }

    override fun run() {
        when {
            bulk -> when (type) {
                LOGIC -> TODO()
                TEXT -> TODO()
            }
            predicative -> when (type) {
                LOGIC -> getTupleSpace<LogicSpace>(tupleSpaceID).tryRead(template).onReadCompletion()
                TEXT -> getTupleSpace<StringSpace>(tupleSpaceID).tryRead(template).onReadCompletion()
            }
            else -> when (type) {
                LOGIC -> getTupleSpace<LogicSpace>(tupleSpaceID).read(template).onReadCompletion()
                TEXT -> getTupleSpace<StringSpace>(tupleSpaceID).read(template).onReadCompletion()
            }
        }
    }
}