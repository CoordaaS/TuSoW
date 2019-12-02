package it.unibo.coordination.linda.logic

import alice.tuprolog.Term
import it.unibo.coordination.prologx.PrologUtils
import it.unibo.presentation.Presentation as Prototype

object Presentation : Prototype by Prototype.default {
    init {
        registerDynamicSerializers(LogicTemplate::class.java) { _, _, template ->
            PrologUtils.termToDynamicObject(template.template)
        }

        registerDynamicSerializers(LogicTuple::class.java) { _, _, tuple ->
            PrologUtils.termToDynamicObject(tuple.value)
        }

        registerDynamicSerializers(Term::class.java) { _, _, term ->
            PrologUtils.termToDynamicObject(term)
        }

//        registerDynamicSerializers(LogicMatch::class.java) { _, _, logicTemplate ->
//            PrologUtils.termToDynamicObject(logicTemplate.)
//        }

    }
}