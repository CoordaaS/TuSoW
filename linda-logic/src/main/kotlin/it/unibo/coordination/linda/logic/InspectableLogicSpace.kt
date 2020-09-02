package it.unibo.coordination.linda.logic

import it.unibo.coordination.Engines
import it.unibo.coordination.linda.core.InspectableTupleSpace
import it.unibo.tuprolog.core.Term

import java.util.concurrent.ExecutorService

interface InspectableLogicSpace : LogicSpace, InspectableTupleSpace<LogicTuple, LogicTemplate, String, Term, LogicMatch> {
    companion object {

        @JvmStatic
        fun local(name: String?, executorService: ExecutorService): InspectableLogicSpace {
            return LogicSpaceImpl(name, executorService)
        }

        @JvmStatic
        fun local(name: String?): InspectableLogicSpace {
            return local(name, Engines.defaultEngine)
        }

        @JvmStatic
        fun local(executorService: ExecutorService): InspectableLogicSpace {
            return local(null, executorService)
        }

        @JvmStatic
        fun local(): InspectableLogicSpace {
            return local(null, Engines.defaultEngine)
        }
    }

}
