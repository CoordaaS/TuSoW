package it.unibo.coordination.linda.cli

import it.unibo.coordination.linda.core.Tuple
import it.unibo.coordination.linda.logic.LogicSpace
import it.unibo.coordination.linda.text.TextualSpace
import java.util.concurrent.CompletableFuture

class GetCommand(
        epilog: String = "",
        name: String? = "get",
        invokeWithoutSubcommand: Boolean = false,
        printHelpOnEmptyArgs: Boolean = false,
        helpTags: Map<String, String> = emptyMap(),
        autoCompleteEnvvar: String? = ""
) : AbstractTupleSpaceCommand(
        help = "Operation aimed at getting all the tuples from a tuple space",
        epilog = epilog,
        name = name,
        invokeWithoutSubcommand = invokeWithoutSubcommand,
        printHelpOnEmptyArgs = printHelpOnEmptyArgs,
        helpTags = helpTags,
        autoCompleteEnvvar = autoCompleteEnvvar)  {

    private fun <T : Tuple<T>, C : Collection<T>> CompletableFuture<C>.defaultHandler() {
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
            TupleSpaceTypes.TEXT -> getTupleSpace<TextualSpace>(tupleSpaceID)
                    .get()
                    .defaultHandler()
        }
    }

}