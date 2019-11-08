package it.unibo.coordination.linda.logic

import alice.tuprolog.SolveInfo
import alice.tuprolog.Term
import alice.tuprolog.exceptions.NoSolutionException
import it.unibo.coordination.linda.core.Match
import java.util.*

internal class LogicMatchImpl(override val template: LogicTemplate, private val solveInfo: SolveInfo?, tuple: LogicTuple?) : LogicMatch {

    override val tuple: Optional<LogicTuple> = Optional.ofNullable(tuple)

    private val cache: Map<String, Term> by lazy {
        if (solveInfo == null)
            emptyMap()
        else try {
            val x = solveInfo.bindingVars.asSequence()
                    .filter { it.link != null }
                    .map { it.name to it.link }
                    .toMap()
            x
        } catch (e: NoSolutionException) {
            emptyMap<String, Term>()
        }
    }

    override val isMatching: Boolean
        get() = solveInfo != null && solveInfo.isSuccess

    override fun get(key: String): Optional<Term> {
        return Optional.ofNullable(toMap()[key])
    }


    override fun toMap(): Map<String, Term> = cache

    override fun toString(): String {
        return LogicMatch.toString(this)
    }

    override fun equals(other: Any?): Boolean {
        return other is LogicMatch && Match.equals(this, other as LogicMatch?)
    }

    override fun hashCode(): Int {
        return Match.hashCode(this)
    }
}
