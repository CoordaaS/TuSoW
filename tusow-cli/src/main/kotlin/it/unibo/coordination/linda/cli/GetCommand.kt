package it.unibo.coordination.linda.cli

import it.unibo.coordination.linda.core.Tuple
import it.unibo.coordination.linda.logic.LogicSpace
import it.unibo.coordination.linda.text.StringSpace
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

    private fun <T : Tuple, C : Collection<T>> CompletableFuture<C>.defaultHandler() {
        await {
            println("Success!")
            if (isEmpty()) {
                println("\tThe tuple space is empty")
            } else {
                println("\tResults:")
                forEachIndexed { i, it ->
                    println("\t\t$i) ${it.value}")
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