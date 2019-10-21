package it.unibo.coordination.linda.cli

import it.unibo.coordination.linda.cli.TupleSpaceTypes.LOGIC
import it.unibo.coordination.linda.cli.TupleSpaceTypes.TEXT
import it.unibo.coordination.linda.core.Match
import it.unibo.coordination.linda.logic.LogicSpace
import it.unibo.coordination.linda.string.StringSpace

class AbsentCommand : AbstractObserveCommand(name="absent") {

    override fun run() {
        when {
            bulk -> TODO("Currently not supported operation")
            predicative -> when (type) {
                LOGIC -> getTupleSpace<LogicSpace>(tupleSpaceID)
                        .tryAbsent(template)
                        .defaultReadHandlerForSingleResult()
                TEXT -> getTupleSpace<StringSpace>(tupleSpaceID)
                        .tryAbsent(template)
                        .defaultReadHandlerForSingleResult()
            }
            else -> when (type) {
                LOGIC -> getTupleSpace<LogicSpace>(tupleSpaceID)
                        .absent(template)
                        .defaultReadHandlerForSingleResult()
                TEXT -> getTupleSpace<StringSpace>(tupleSpaceID)
                        .absent(template)
                        .defaultReadHandlerForSingleResult()
            }
        }
    }

    override fun <T, TT, K, V, M : Match<T, TT, K, V>> M.isSuccess(): Boolean = !isMatching()

    override fun <T, TT, K, V, M : Match<T, TT, K, V>> M.getResult(): Any =
            if (isMatching()) getTuple() as Any else getTemplate() as Any

    override fun <T, TT, K, V, M : Match<T, TT, K, V>, C : Collection<out M>> C.isSuccess(): Boolean = isEmpty()
}