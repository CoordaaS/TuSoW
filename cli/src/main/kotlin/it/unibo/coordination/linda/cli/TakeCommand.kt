package it.unibo.coordination.linda.cli

import it.unibo.coordination.linda.cli.TupleSpaceTypes.LOGIC
import it.unibo.coordination.linda.cli.TupleSpaceTypes.TEXT
import it.unibo.coordination.linda.logic.LogicSpace
import it.unibo.coordination.linda.string.StringSpace

class TakeCommand : AbstractObserveCommand(name="take") {

    override fun run() {
        when {
            bulk -> when (type) {
                LOGIC -> getTupleSpace<LogicSpace>(tupleSpaceID)
                        .takeAll(template)
                        .defaultReadHandlerForMultipleResult()
                TEXT -> getTupleSpace<StringSpace>(tupleSpaceID)
                        .takeAll(template)
                        .defaultReadHandlerForMultipleResult()
            }
            predicative -> when (type) {
                LOGIC -> getTupleSpace<LogicSpace>(tupleSpaceID)
                        .tryTake(template)
                        .defaultReadHandlerForSingleResult()
                TEXT -> getTupleSpace<StringSpace>(tupleSpaceID)
                        .tryTake(template)
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