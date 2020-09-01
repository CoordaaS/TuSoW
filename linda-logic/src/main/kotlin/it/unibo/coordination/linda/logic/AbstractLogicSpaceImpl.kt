package it.unibo.coordination.linda.logic

import it.unibo.coordination.linda.core.impl.AbstractTupleSpace
import it.unibo.coordination.linda.logic.LogicMatch.Companion.failed
import it.unibo.tuprolog.collections.MutableClauseMultiSet
import it.unibo.tuprolog.collections.RetrieveResult
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Term
import java.util.concurrent.ExecutorService
import java.util.stream.Stream
import kotlin.streams.asStream

internal abstract class AbstractLogicSpaceImpl(name: String?, executor: ExecutorService) :
        AbstractTupleSpace<LogicTuple, LogicTemplate, String, Term, LogicMatch>(name, executor), InspectableLogicSpace {

    private val tupleStore = MutableClauseMultiSet.empty()

    override fun lookForTuples(template: LogicTemplate, limit: Int): Stream<LogicMatch> =
            tupleStore[Fact.of(template.asTerm())]
                    .filterIsInstance<Fact>()
                    .map { it.head }
                    .map(LogicTuple::of)
                    .map(template::matchWith)
                    .take(limit)
                    .asStream()

    override fun lookForTuple(template: LogicTemplate): LogicMatch =
            lookForTuples(template, 1).findAny().orElseGet { failed(template) }

    override fun retrieveTuples(template: LogicTemplate, limit: Int): Stream<LogicMatch> = sequence {
        for (i in 0 until limit) {
            when (val taken = tupleStore.retrieve(Fact.of(template.asTerm()))) {
                is RetrieveResult.Success<*> -> yieldAll(taken.clauses)
                else -> break
            }
        }
    }.filterIsInstance<Fact>()
            .map { it.head }
            .map(LogicTuple::of)
            .map(template::matchWith)
            .asStream()

    override fun retrieveTuple(template: LogicTemplate): LogicMatch =
            when (val taken = tupleStore.retrieve(Fact.of(template.asTerm()))) {
                is RetrieveResult.Success<*> -> template.matchWith(LogicTuple.of(taken.firstClause.head!!))
                else -> failed(template)
            }

    override fun insertTuple(tuple: LogicTuple) {
        tupleStore.add(Fact.of(tuple.asTerm()))
    }

    override val allTuples: Stream<LogicTuple>
        get() = tupleStore.asSequence()
                .filterIsInstance<Fact>()
                .map { it.head }
                .map(LogicTuple::of)
                .asStream()

    override fun countTuples(): Int = tupleStore.size

    override fun match(template: LogicTemplate, tuple: LogicTuple): LogicMatch = template.matchWith(tuple)

    override fun failedMatch(template: LogicTemplate): LogicMatch = failed(template)
}