package it.unibo.coordination.linda.cli

import it.unibo.coordination.linda.logic.LogicSpace
import it.unibo.coordination.linda.text.TextualSpace
import java.util.concurrent.CompletableFuture

class CountCommand(
        epilog: String = "",
        name: String? = "count",
        invokeWithoutSubcommand: Boolean = false,
        printHelpOnEmptyArgs: Boolean = false,
        helpTags: Map<String, String> = emptyMap(),
        autoCompleteEnvvar: String? = ""
) : AbstractTupleSpaceCommand(
        help = "Operation aimed at counting how many tuples a tuple space contains",
        epilog = epilog,
        name = name,
        invokeWithoutSubcommand = invokeWithoutSubcommand,
        printHelpOnEmptyArgs = printHelpOnEmptyArgs,
        helpTags = helpTags,
        autoCompleteEnvvar = autoCompleteEnvvar)  {

    fun CompletableFuture<Int>.defaultHandler() {
        await {
            println("Success!")
            println("\tResult: $this")
        }
    }

    override fun run() {
        when (type) {
            TupleSpaceTypes.LOGIC -> getTupleSpace<LogicSpace>(tupleSpaceID)
                    .getSize()
                    .defaultHandler()
            TupleSpaceTypes.TEXT -> getTupleSpace<TextualSpace>(tupleSpaceID)
                    .getSize()
                    .defaultHandler()
        }
    }

}