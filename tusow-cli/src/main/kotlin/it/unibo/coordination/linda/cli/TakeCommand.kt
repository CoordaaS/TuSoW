package it.unibo.coordination.linda.cli

import it.unibo.coordination.linda.cli.TupleSpaceTypes.LOGIC
import it.unibo.coordination.linda.cli.TupleSpaceTypes.TEXT
import it.unibo.coordination.linda.logic.LogicSpace
import it.unibo.coordination.linda.text.TextualSpace

class TakeCommand(
        help: String = "",
        epilog: String = "",
        name: String? = "take",
        invokeWithoutSubcommand: Boolean = false,
        printHelpOnEmptyArgs: Boolean = false,
        helpTags: Map<String, String> = emptyMap(),
        autoCompleteEnvvar: String? = ""
) : AbstractObserveCommand(help, epilog, name, invokeWithoutSubcommand, printHelpOnEmptyArgs, helpTags, autoCompleteEnvvar)  {

    override fun run() {
        when {
            bulk -> when (type) {
                LOGIC -> getTupleSpace<LogicSpace>(tupleSpaceID)
                        .takeAll(template)
                        .defaultHandlerForMultipleResult()
                TEXT -> getTupleSpace<TextualSpace>(tupleSpaceID)
                        .takeAll(template)
                        .defaultHandlerForMultipleResult()
            }
            predicative -> when (type) {
                LOGIC -> getTupleSpace<LogicSpace>(tupleSpaceID)
                        .tryTake(template)
                        .defaultHandlerForSingleResult()
                TEXT -> getTupleSpace<TextualSpace>(tupleSpaceID)
                        .tryTake(template)
                        .defaultHandlerForSingleResult()
            }
            else -> when (type) {
                LOGIC -> getTupleSpace<LogicSpace>(tupleSpaceID)
                        .take(template)
                        .defaultHandlerForSingleResult()
                TEXT -> getTupleSpace<TextualSpace>(tupleSpaceID)
                        .take(template)
                        .defaultHandlerForSingleResult()
            }
        }
    }
}