package it.unibo.coordination.linda.cli

import it.unibo.coordination.linda.logic.LogicSpace
import it.unibo.coordination.linda.text.StringSpace
import java.util.concurrent.CompletableFuture

class CountCommand(
        help: String = "",
        epilog: String = "",
        name: String? = "count",
        invokeWithoutSubcommand: Boolean = false,
        printHelpOnEmptyArgs: Boolean = false,
        helpTags: Map<String, String> = emptyMap(),
        autoCompleteEnvvar: String? = ""
) : AbstractTupleSpaceCommand(help, epilog, name, invokeWithoutSubcommand, printHelpOnEmptyArgs, helpTags, autoCompleteEnvvar) {

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
            TupleSpaceTypes.TEXT -> getTupleSpace<StringSpace>(tupleSpaceID)
                    .getSize()
                    .defaultHandler()
        }
    }

}