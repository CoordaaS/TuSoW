package it.unibo.coordination.linda.cli

import it.unibo.coordination.linda.logic.LogicSpace
import it.unibo.coordination.linda.logic.LogicTuple
import it.unibo.coordination.linda.text.StringTuple
import it.unibo.coordination.linda.text.TextualSpace

class WriteCommand(
        help: String = "",
        epilog: String = "",
        name: String? = "write",
        invokeWithoutSubcommand: Boolean = false,
        printHelpOnEmptyArgs: Boolean = false,
        helpTags: Map<String, String> = emptyMap(),
        autoCompleteEnvvar: String? = ""
) : AbstractUpdateCommand(help, epilog, name, invokeWithoutSubcommand, printHelpOnEmptyArgs, helpTags, autoCompleteEnvvar) {

    override fun run() {
        if (asynchronous) {
            when {
                bulk -> when (type) {
                    TupleSpaceTypes.LOGIC -> tuples.map { LogicTuple.of(it) }.let {
                            getTupleSpace<LogicSpace>(tupleSpaceID)
                                    .writeAll(it)
                                    .defaultAsyncHandlerForMultipleResult(it)
                        }
                    TupleSpaceTypes.TEXT -> tuples.map { StringTuple.of(it) }.let {
                        getTupleSpace<TextualSpace>(tupleSpaceID)
                                .writeAll(it)
                                .defaultAsyncHandlerForMultipleResult(it)
                    }
                }
                else -> when (type) {
                    TupleSpaceTypes.LOGIC -> tuples[0].let { LogicTuple.of(it) }.let {
                        getTupleSpace<LogicSpace>(tupleSpaceID)
                                .write(it)
                                .defaultAsyncHandlerForSingleResult(it)
                    }
                    TupleSpaceTypes.TEXT -> tuples[0].let(StringTuple::of).let {
                        getTupleSpace<TextualSpace>(tupleSpaceID)
                                .write(it)
                                .defaultAsyncHandlerForSingleResult(it)
                    }
                }
            }
        } else {
            when {
                bulk -> when (type) {
                    TupleSpaceTypes.LOGIC -> getTupleSpace<LogicSpace>(tupleSpaceID)
                            .writeAll(tuples.map { LogicTuple.of(it) })
                            .defaultHandlerForMultipleResult()
                    TupleSpaceTypes.TEXT -> getTupleSpace<TextualSpace>(tupleSpaceID)
                            .writeAll(tuples.map { StringTuple.of(it) })
                            .defaultHandlerForMultipleResult()
                }
                else -> when (type) {
                    TupleSpaceTypes.LOGIC -> getTupleSpace<LogicSpace>(tupleSpaceID)
                            .write(LogicTuple.of(tuples[0]))
                            .defaultHandlerForSingleResult()
                    TupleSpaceTypes.TEXT -> getTupleSpace<TextualSpace>(tupleSpaceID)
                            .write(StringTuple.of(tuples[0]))
                            .defaultHandlerForSingleResult()
                }
            }
        }
    }

}