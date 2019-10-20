package it.unibo.coordination.linda.cli

import it.unibo.coordination.linda.cli.TupleSpaceTypes.LOGIC
import it.unibo.coordination.linda.cli.TupleSpaceTypes.TEXT
import it.unibo.coordination.linda.logic.LogicSpace
import it.unibo.coordination.linda.string.StringSpace

class ReadCommand : AbstractObserveCommand(name="read") {

    override fun run() {
        when {
            bulk -> when (type) {
                LOGIC -> getTupleSpace<LogicSpace>(tupleSpaceID)
                        .readAll(template)
                        .defaultReadHandlerForMultipleResult()
                TEXT -> getTupleSpace<StringSpace>(tupleSpaceID)
                        .readAll(template)
                        .defaultReadHandlerForMultipleResult()
            }
            predicative -> when (type) {
                LOGIC -> getTupleSpace<LogicSpace>(tupleSpaceID)
                        .tryRead(template)
                        .defaultReadHandlerForSingleResult()
                TEXT -> getTupleSpace<StringSpace>(tupleSpaceID)
                        .tryRead(template)
                        .defaultReadHandlerForSingleResult()
            }
            else -> when (type) {
                LOGIC -> getTupleSpace<LogicSpace>(tupleSpaceID)
                        .read(template)
                        .defaultReadHandlerForSingleResult()
                TEXT -> getTupleSpace<StringSpace>(tupleSpaceID)
                        .read(template)
                        .defaultReadHandlerForSingleResult()
            }
        }
    }
}