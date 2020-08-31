package it.unibo.cooordination.respect.core

import it.unibo.coordination.linda.core.OperationPhase
import it.unibo.coordination.linda.core.Template
import it.unibo.coordination.linda.core.Tuple

// <event_focus>_<event_selector>
// <event_focus> := current | event | start
// <event_selector> := predicate | tuple | source | target | time | space
interface InternalEvent<T : Tuple<T>, TT : Template<T>> {
    val trigger: ExternalEvent<T, TT> // <primitive>(<args>) | time(...) |
    val phase: OperationPhase
    val source: EntityID // who provoked event
    val target: EntityID // the event's target
    val time: Long //
    // where

}