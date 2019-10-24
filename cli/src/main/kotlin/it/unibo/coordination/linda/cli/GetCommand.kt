package it.unibo.coordination.linda.cli

import it.unibo.coordination.linda.logic.LogicSpace
import it.unibo.coordination.linda.string.StringSpace
import java.util.concurrent.CompletableFuture

class GetCommand(
        help: String = "",
        epilog: String = "",
        name: String? = "get",
        invokeWithoutSubcommand: Boolean = false,
        printHelpOnEmptyArgs: Boolean = false,
        helpTags: Map<String, String> = emptyMap(),
        autoCompleteEnvvar: String? = ""
) : AbstractTupleSpaceCommand(help, epilog, name, invokeWithoutSubcommand, printHelpOnEmptyArgs, helpTags, autoCompleteEnvvar) {

    fun <T, C : Collection<out T>> CompletableFuture<C>.defaultHandler() {
        await {
            println("Success!")
            forEach {
                println("\tResults:")
                forEach {
                    println("\t\t$it")
                }
            }
        }
    }

    override fun run() {
        when (type) {
            TupleSpaceTypes.LOGIC -> getTupleSpace<LogicSpace>(tupleSpaceID)
                    .get()
                    .defaultHandler()
            TupleSpaceTypes.TEXT -> getTupleSpace<StringSpace>(tupleSpaceID)
                    .get()
                    .defaultHandler()
        }
    }

}