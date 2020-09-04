package it.unibo.cooordination.respect.logic

import it.unibo.cooordination.respect.core.SpecificationTemplate
import it.unibo.coordination.linda.logic.LogicTemplate
import it.unibo.coordination.linda.logic.LogicTuple
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.core.Tuple

@Suppress("MemberVisibilityCanBePrivate", "CanBeParameter")
class LogicSpecificationTemplate(
        val logicEvent: Struct,
        val logicGuards: Array<Struct> = arrayOf(Truth.TRUE),
        val logicBody: Struct
) : SpecificationTemplate<LogicTuple, LogicTemplate, LogicSpecificationTuple> {

    override fun matchWith(tuple: LogicSpecificationTuple): LogicSpecificationMatch =
            LogicSpecificationMatch.of(this, tuple)

    val template: Struct = Struct.of("reaction", logicEvent, Tuple.wrapIfNeeded(*logicGuards), logicBody)
}