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

class ReadCommand : AbstractTupleSpaceCommand(name = "read") {

    val template: String by argument("TEMPLATE")
    val bulk: Boolean by option("-b", "--bulk").flag(default = false)
    val predicative: Boolean by option("-p", "--predicative").flag(default = false)

    protected fun <T, TT, K, V, M : Match<T, TT, K, V>> CompletableFuture<M>.defaultReadHandlerForSingleResult(): Unit {
        onSingleMatchCompletion {
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

    protected fun <T, TT, K, V, M : Match<T, TT, K, V>, C : Collection<out M>> CompletableFuture<C>.defaultReadHandlerForMultipleResult(): Unit {
        onMultipleMatchCompletion {
            println(if (isNotEmpty()) "Success!" else "Failure!")
            if (isNotEmpty()) {
                println("Success!")
                forEach {
                    println("\tResult: ${it.getTuple()}")
                    it.toMap().let {
                        if (it.isNotEmpty()) {
                            println("\tWhere:")
                            it.forEach { k, v ->
                                println("\t\t$k = $v")
                            }
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
                LOGIC -> getTupleSpace<LogicSpace>(tupleSpaceID)
                        .readAll(template)
                        .defaultReadHandlerForMultipleResult()
                TEXT -> getTupleSpace<StringSpace>(tupleSpaceID)
                        .readAll(template)
                        .defaultReadHandlerForMultipleResult()
            }
            predicative -> when (type) {
                LOGIC -> getTupleSpace<LogicSpace>(tupleSpaceID)
                        .tryRead(template)
                        .defaultReadHandlerForSingleResult()
                TEXT -> getTupleSpace<StringSpace>(tupleSpaceID)
                        .tryRead(template)
                        .defaultReadHandlerForSingleResult()
            }
            else -> when (type) {
                LOGIC -> getTupleSpace<LogicSpace>(tupleSpaceID)
                        .read(template)
                        .defaultReadHandlerForSingleResult()
                TEXT -> getTupleSpace<StringSpace>(tupleSpaceID)
                        .read(template)
                        .defaultReadHandlerForSingleResult()
            }
        }
    }
}