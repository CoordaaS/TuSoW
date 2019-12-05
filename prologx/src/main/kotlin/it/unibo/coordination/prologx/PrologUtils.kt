package it.unibo.coordination.prologx

import alice.tuprolog.*
import alice.tuprolog.exceptions.InvalidTermException
import alice.tuprolog.exceptions.NoMoreSolutionException
import alice.tuprolog.exceptions.NoSolutionException
import alice.tuprolog.presentation.TermUtils
import java.util.*
import java.util.stream.IntStream
import java.util.stream.Stream
import kotlin.streams.asStream

object PrologUtils {
    @JvmStatic
    fun assertOn(engine: Prolog, term: Term?): Boolean {
        return try {
            val si = engine.solve(assertTerm(term))
            si.isSuccess
        } finally {
            engine.solveEnd()
        }
    }

    @JvmStatic
    fun retractFrom(engine: Prolog, term: Term?): Optional<Term> {
        return try {
            val si = engine.solve(retractTerm(term))
            if (si.isSuccess) {
                val retraction = si.solution as Struct
                return Optional.of(retraction.getArg(0))
            }
            Optional.empty()
        } catch (e: NoSolutionException) {
            Optional.empty()
        } finally {
            engine.solveEnd()
        }
    }

    @JvmStatic
    fun solveStream(engine: Prolog, goal: Term?): Stream<SolveInfo> {
        return sequence {
            var first = true
            var last: SolveInfo? = null

            while (true) {
                if (first) {
                    last = engine.solve(goal)
                    if (!last.isSuccess) {
                        engine.solveEnd()
                    }
                    first = false
                } else {
                    try {
                        if (last!!.isSuccess && last.hasOpenAlternatives()) {
                            last = engine.solveNext()
                        } else {
                            engine.solveEnd()
                            break
                        }
                    } catch (e: NoMoreSolutionException) {
                        break
                    }
                }
                yield(last)
            }
        }.takeWhile {
            it !== null && it.isSuccess
        }.map { it!! }.asStream()
    }

    @JvmStatic
    fun solutionsStream(engine: Prolog, goal: Term?): Stream<Term> {
        return solveStream(engine, goal).map {
            try {
                return@map it.solution
            } catch (e: NoSolutionException) {
                throw IllegalStateException(e)
            }
        }
    }

    @JvmStatic
    @Deprecated("")
    fun listToStream(term: Term): Stream<Term> {
        return if (term.isList) {
            listToStream(term as Struct)
        } else {
            throw IllegalArgumentException("Not a list: $term")
        }
    }

    @JvmStatic
    @Deprecated("")
    fun listToStream(list: Struct): Stream<Term> {
        return list.listStream().map { it!! }
    }

    @JvmStatic
    fun unificationTerm(`var`: String?, term: Term?): Struct {
        return unificationTerm(Var.of(`var`), term)
    }

    @JvmStatic
    fun unificationTerm(term1: Term?, term2: Term?): Struct {
        return Struct.of("=", term1, term2)
    }

    @JvmStatic
    fun assertTerm(term: Term?): Struct {
        return Struct.of("assert", term)
    }

    @JvmStatic
    fun retractTerm(term: Term?): Struct {
        return Struct.of("retract", term)
    }

    @JvmStatic
    fun anyToTerm(payload: Any?): Term {
        return if (payload == null) {
            Var.anonymous()
        } else if (payload is Term) {
            payload
        } else if (payload is PrologSerializable) {
            payload.toTerm()
        } else {
            try {
                Term.createTerm(payload.toString())
            } catch (e: InvalidTermException) {
                Struct.atom(payload.toString())
            }
        }
    }

    @JvmStatic
    private val HELPER = Prolog()

    @JvmStatic
    fun dynamicObjectToTerm(`object`: Any?): Term {
        return TermUtils.dynamicObjectToTerm(`object`)
    }

    @JvmStatic
    fun termToDynamicObject(term: Term?): Any {
        return TermUtils.termToDynamicObject(term)
    }

    @JvmStatic
    fun argumentsStream(struct: Struct): Stream<Term> {
        return IntStream.range(0, struct.arity).mapToObj { index: Int -> struct.getArg(index) }
    }
}