package it.unibo.coordination.linda.cli

import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import it.unibo.coordination.linda.core.Match
import java.util.concurrent.CompletableFuture

abstract class AbstractObserveCommand(
        help: String = "",
        epilog: String = "",
        name: String? = null,
        invokeWithoutSubcommand: Boolean = false,
        printHelpOnEmptyArgs: Boolean = false,
        helpTags: Map<String, String> = emptyMap(),
        autoCompleteEnvvar: String? = ""
) : AbstractTupleSpaceCommand(help, epilog, name, invokeWithoutSubcommand, printHelpOnEmptyArgs, helpTags, autoCompleteEnvvar) {

    val template: String by argument("TEMPLATE")
    val bulk: Boolean by option("-b", "--bulk").flag(default = false)
    val predicative: Boolean by option("-p", "--predicative").flag(default = false)

    protected fun <T, TT, K, V, M : Match<T, TT, K, V>> CompletableFuture<M>.defaultReadHandlerForSingleResult() {
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
                    with(it.toMap()) {
                        if (isNotEmpty()) {
                            println("\tWhere:")
                            forEach { k, v ->
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

}