package it.unibo.coordination.linda.logic

import alice.tuprolog.Term
import it.unibo.coordination.Engines
import it.unibo.coordination.linda.core.InspectableTupleSpace

import java.util.concurrent.ExecutorService

interface InspectableLogicSpace : LogicSpace, InspectableTupleSpace<LogicTuple, LogicTemplate, String, Term, LogicMatch> {
    companion object {

        @JvmStatic
        fun create(name: String?, executorService: ExecutorService): InspectableLogicSpace {
            return DeterministicLogicSpaceImpl(name, executorService)
        }

        @JvmStatic
        fun create(name: String?): InspectableLogicSpace {
            return DeterministicLogicSpaceImpl(name, Engines.defaultEngine)
        }

        @JvmStatic
        fun create(executorService: ExecutorService): InspectableLogicSpace {
            return create(null, executorService)
        }
    }

}
