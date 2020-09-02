package it.unibo.coordination.linda.logic

import it.unibo.coordination.linda.core.impl.AbstractTupleSpace
import it.unibo.coordination.linda.logic.LogicMatch.Companion.failed
import it.unibo.tuprolog.collections.MutableClauseMultiSet
import it.unibo.tuprolog.collections.RetrieveResult
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Term
import java.util.concurrent.ExecutorService
import java.util.stream.Stream
import kotlin.streams.asStream

internal abstract class AbstractLogicSpaceImpl(name: String?, executor: ExecutorService) :
        AbstractTupleSpace<LogicTuple, LogicTemplate, String, Term, LogicMatch>(name, executor), InspectableLogicSpace {

    private val tupleStore = MutableClauseMultiSet.empty()

    private fun LogicTuple.asFact(): Fact = Fact.of(asTerm())

    private fun LogicTemplate.asQuery(): Fact = Fact.of(toTuple().asTerm())

    private fun MutableClauseMultiSet.retrieve(pattern: Clause, limit: Int): Sequence<Clause> =
            sequence {
                for (i in 0 until limit) {
                    when (val taken = retrieve(pattern)) {
                        is RetrieveResult.Success<*> -> yieldAll(taken.clauses)
                        else -> break
                    }
                }
            }

    override fun lookForTuples(template: LogicTemplate, limit: Int): Stream<LogicMatch> =
            tupleStore[template.asQuery()]
                    .filterIsInstance<Fact>()
                    .map { it.head }
                    .map(LogicTuple::of)
                    .map(template::matchWith)
                    .take(limit)
                    .asStream()

    override fun lookForTuple(template: LogicTemplate): LogicMatch =
            lookForTuples(template, 1).findAny().orElseGet { failed(template) }

    private fun retrieveTuplesSeq(template: LogicTemplate, limit: Int): Sequence<LogicMatch> =
            tupleStore.retrieve(template.asQuery(), limit)
                    .filterIsInstance<Fact>()
                    .map { it.head }
                    .map(LogicTuple::of)
                    .map(template::matchWith)

    override fun retrieveTuples(template: LogicTemplate, limit: Int): Stream<LogicMatch> =
            retrieveTuplesSeq(template, limit)
                    .asStream()

    override fun retrieveTuple(template: LogicTemplate): LogicMatch =
            retrieveTuplesSeq(template, 1).firstOrNull() ?: failedMatch(template)

    override fun insertTuple(tuple: LogicTuple) {
        tupleStore.add(tuple.asFact())
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