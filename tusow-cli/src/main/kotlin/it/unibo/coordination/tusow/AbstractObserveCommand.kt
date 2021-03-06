package it.unibo.coordination.tusow

import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import it.unibo.coordination.linda.core.Match
import it.unibo.coordination.linda.core.Template
import it.unibo.coordination.linda.core.Tuple
import java.util.concurrent.CompletableFuture

abstract class AbstractObserveCommand(
        action: String = "observing",
        help: String = "Operation aimed at $action one or more tuples given a template",
        epilog: String = "",
        name: String? = null,
        invokeWithoutSubcommand: Boolean = false,
        printHelpOnEmptyArgs: Boolean = false,
        helpTags: Map<String, String> = emptyMap(),
        autoCompleteEnvvar: String? = ""
) : AbstractTupleSpaceCommand(help, epilog, name, invokeWithoutSubcommand, printHelpOnEmptyArgs, helpTags, autoCompleteEnvvar) {

    val template: String by argument(
            name="TEMPLATE",
            help = "The template the tuple(s) to be observed should match. Its format depends on the -t option"
        )
    val predicative: Boolean by option(
            "-p", "--predicative",
            help = "Makes this operation predicative, i.e., non-blocking. Defaults to false, i.e. operations are blocking by default"
    ).flag(default = false)

    open protected fun <T : Tuple<T>, TT : Template<T>, K, V, M : Match<T, TT, K, V>> M.isSuccess(): Boolean = isMatching

    open protected fun <T : Tuple<T>, TT : Template<T>, K, V, M : Match<T, TT, K, V>> M.getResult(): Any = tuple.get().value

    open protected fun <T : Tuple<T>, TT : Template<T>, K, V, M : Match<T, TT, K, V>, C : Collection<M>> C.isSuccess(): Boolean = isNotEmpty()

    protected fun <T : Tuple<T>, TT : Template<T>, K, V, M : Match<T, TT, K, V>, C : Collection<M>> CompletableFuture<C>.defaultHandlerForMultipleResult() {
        await {
            if (isSuccess()) {
                println("Success!")
                forEach {
                    println("\t- Result: ${it.getResult()}")
                    with(it.toMap()) {
                        if (isNotEmpty()) {
                            println("\t  Where:")
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

    protected fun <T : Tuple<T>, TT : Template<T>, K, V, M : Match<T, TT, K, V>> CompletableFuture<M>.defaultHandlerForSingleResult() {
        await {
            if (isSuccess()) {
                println("Success!")
                println("\tResult: ${getResult()}")
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

}