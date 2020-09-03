package it.unibo.coordination.linda.core

import it.unibo.coordination.linda.core.traits.Inspectability
import it.unibo.coordination.linda.core.traits.InspectabilityOperationalPrimitives

interface InspectableTupleSpace<T : Tuple<T>, TT : Template<T>, K, V, M : Match<T, TT, K, V>>
    : TupleSpace<T, TT, K, V, M>,
        Inspectability<T, TT>,
        InspectabilityOperationalPrimitives<T, TT>