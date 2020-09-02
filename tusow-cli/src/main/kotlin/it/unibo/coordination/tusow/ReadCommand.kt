package it.unibo.coordination.tusow

import it.unibo.coordination.linda.logic.LogicSpace
import it.unibo.coordination.linda.text.TextualSpace
import it.unibo.coordination.tusow.TupleSpaceTypes.LOGIC
import it.unibo.coordination.tusow.TupleSpaceTypes.TEXT

class ReadCommand(
        epilog: String = "",
        name: String? = "read",
        invokeWithoutSubcommand: Boolean = false,
        printHelpOnEmptyArgs: Boolean = false,
        helpTags: Map<String, String> = emptyMap(),
        autoCompleteEnvvar: String? = ""
) : AbstractObserveCommand(
        action = "reading",
        epilog = epilog,
        name = name,
        invokeWithoutSubcommand = invokeWithoutSubcommand,
        printHelpOnEmptyArgs = printHelpOnEmptyArgs,
        helpTags = helpTags,
        autoCompleteEnvvar = autoCompleteEnvvar)  {

    override fun run() {
        when {
            bulk -> when (type) {
                LOGIC -> getTupleSpace<LogicSpace>(tupleSpaceID)
                        .readAll(template)
                        .defaultHandlerForMultipleResult()
                TEXT -> getTupleSpace<TextualSpace>(tupleSpaceID)
                        .readAll(template)
                        .defaultHandlerForMultipleResult()
            }
            predicative -> when (type) {
                LOGIC -> getTupleSpace<LogicSpace>(tupleSpaceID)
                        .tryRead(template)
                        .defaultHandlerForSingleResult()
                TEXT -> getTupleSpace<TextualSpace>(tupleSpaceID)
                        .tryRead(template)
                        .defaultHandlerForSingleResult()
            }
            else -> when (type) {
                LOGIC -> getTupleSpace<LogicSpace>(tupleSpaceID)
                        .read(template)
                        .defaultHandlerForSingleResult()
                TEXT -> getTupleSpace<TextualSpace>(tupleSpaceID)
                        .read(template)
                        .defaultHandlerForSingleResult()
            }
        }
    }
}