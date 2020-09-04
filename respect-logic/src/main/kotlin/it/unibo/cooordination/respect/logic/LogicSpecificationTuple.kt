package it.unibo.cooordination.respect.logic

import it.unibo.cooordination.respect.core.*
import it.unibo.coordination.linda.logic.LogicTemplate
import it.unibo.coordination.linda.logic.LogicTuple
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.core.Tuple
import it.unibo.tuprolog.solve.ClassicSolverFactory
import it.unibo.tuprolog.solve.MutableSolver
import it.unibo.tuprolog.solve.Solution

data class LogicSpecificationTuple private constructor(
        val logicEvent: Struct,
        val logicGuards: Array<Struct> = arrayOf(Truth.TRUE),
        val logicBody: Struct
) : SpecificationTuple<LogicTuple, LogicTemplate, LogicSpecificationTuple> {

    companion object {
        fun of(
                logicEvent: Struct,
                firstGuard: Struct,
                vararg logicGuards: Struct,
                logicBody: Struct
        ) = LogicSpecificationTuple(logicEvent, arrayOf(firstGuard, *logicGuards), logicBody)

        fun of(
                logicEvent: Struct,
                logicGuard: Struct,
                logicBody: Struct
        ): LogicSpecificationTuple = of(logicEvent, logicGuard, logicGuards = emptyArray(), logicBody)

        fun of(
                logicEvent: Struct,
                logicBody: Struct
        ): LogicSpecificationTuple = of(logicEvent, Truth.TRUE, logicBody)
    }

    private fun newSolver(): MutableSolver =
            ClassicSolverFactory.mutableSolverWithDefaultBuiltins()

    override val event: ExternalEvent<LogicTuple, LogicTemplate> by lazy {
        TODO("Convert logicEvent from Struct to ExternalEvent")
    }

    override val guards: GuardSet by lazy {
        TODO("Convert each item in logicGuards as a Guard")
    }

    override fun callback(reactionContext: ReactionContext<LogicTuple, LogicTemplate>, tupleCentre: TupleCentreInternalAPI<LogicTuple, LogicTemplate, *, *, *, LogicSpecificationTuple, *, *>) {
        val solver = newSolver()
        // TODO load observation predicates into the solver's static kb
        // TODO load Linda-related predicates into the solver via Libraries, provoking the invocations of methods from `tupleCentre` whenever the resolution process meets a Linda-predicate invocation
        when (val solution = solver.solve(logicBody).first()) {
            is Solution.Yes -> TODO("handle successful reaction")
            is Solution.No -> throw ReactionException()
            is Solution.Halt -> throw ReactionException(solution.exception)
        }
    }

    override val value: Struct = Struct.of("reaction", logicEvent, Tuple.wrapIfNeeded(*logicGuards), logicBody)


}