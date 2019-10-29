package it.unibo.coordination.linda.cli

import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.multiple
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import it.unibo.coordination.linda.core.Tuple
import java.util.concurrent.CompletableFuture

abstract class AbstractUpdateCommand(
        help: String = "",
        epilog: String = "",
        name: String? = null,
        invokeWithoutSubcommand: Boolean = false,
        printHelpOnEmptyArgs: Boolean = false,
        helpTags: Map<String, String> = emptyMap(),
        autoCompleteEnvvar: String? = ""
) : AbstractTupleSpaceCommand(help, epilog, name, invokeWithoutSubcommand, printHelpOnEmptyArgs, helpTags, autoCompleteEnvvar) {

    val tuples: List<String> by argument("TUPLE").multiple(required = true)
    val asynchronous: Boolean by option("-A", "--asynch").flag(default = false)

    protected fun<T : Tuple> CompletableFuture<T>.defaultHandlerForSingleResult() {
        await {
            println("Success!")
            println("\tTuple $value has been inserted")
        }
    }

    protected fun<T : Tuple> CompletableFuture<T>.defaultAsyncHandlerForSingleResult(input: T) {
        println("Success!")
        println("\tTuple ${input.value} has been inserted")
    }

    protected fun<T : Tuple, C : Collection<T>> CompletableFuture<C>.defaultHandlerForMultipleResult() {
        await {
            println("Success!")
            println("\tThe following tuples have been inserted:")
            forEachIndexed { i, t ->
                println("\t\t${i + 1}) ${t.value}")
            }
        }
    }

    protected fun<T : Tuple, C : Collection<T>> CompletableFuture<C>.defaultAsyncHandlerForMultipleResult(input: Collection<T>) {
        print("Success!")
        println("\tThe following tuples have been inserted:")
        input.forEachIndexed { i, t ->
            println("\t\t${i + 1}) ${t.value}")
        }
    }

}