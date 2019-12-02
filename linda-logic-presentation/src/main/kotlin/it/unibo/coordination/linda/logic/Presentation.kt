package it.unibo.coordination.linda.logic

import alice.tuprolog.Term
import it.unibo.coordination.prologx.PrologUtils
import it.unibo.presentation.Presentation as Prototype

object Presentation : Prototype by Prototype.default {
    init {
        registerDynamicSerializers(LogicTemplate::class.java) { _, _, logicTemplate ->
            PrologUtils.termToDynamicObject(logicTemplate.template)
        }

    }
}