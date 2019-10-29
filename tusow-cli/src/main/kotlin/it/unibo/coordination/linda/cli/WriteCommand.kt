package it.unibo.coordination.linda.cli

import it.unibo.coordination.linda.logic.LogicSpace
import it.unibo.coordination.linda.logic.LogicTuple
import it.unibo.coordination.linda.string.StringSpace
import it.unibo.coordination.linda.string.StringTuple

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
                        getTupleSpace<StringSpace>(tupleSpaceID)
                                .writeAll(it)
                                .defaultAsyncHandlerForMultipleResult(it)
                    }
                }
                else -> when (type) {
                    TupleSpaceTypes.LOGIC -> tuples[0].let(LogicTuple::of).let {
                        getTupleSpace<LogicSpace>(tupleSpaceID)
                                .write(it)
                                .defaultAsyncHandlerForSingleResult(it)
                    }
                    TupleSpaceTypes.TEXT -> tuples[0].let(StringTuple::of).let {
                        getTupleSpace<StringSpace>(tupleSpaceID)
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
                    TupleSpaceTypes.TEXT -> getTupleSpace<StringSpace>(tupleSpaceID)
                            .writeAll(tuples.map { StringTuple.of(it) })
                            .defaultHandlerForMultipleResult()
                }
                else -> when (type) {
                    TupleSpaceTypes.LOGIC -> getTupleSpace<LogicSpace>(tupleSpaceID)
                            .write(LogicTuple.of(tuples[0]))
                            .defaultHandlerForSingleResult()
                    TupleSpaceTypes.TEXT -> getTupleSpace<StringSpace>(tupleSpaceID)
                            .write(StringTuple.of(tuples[0]))
                            .defaultHandlerForSingleResult()
                }
            }
        }
    }

}