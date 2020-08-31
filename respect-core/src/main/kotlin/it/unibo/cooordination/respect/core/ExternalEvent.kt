package it.unibo.cooordination.respect.core

import org.apache.commons.collections4.MultiSet

sealed class ExternalEvent<T, TT> {

    data class CoordinationEvent<T, TT> (
            val primitive: CoordinationPrimitive,
            val argumentTuples: List<T>,
            val argumentTemplates: List<TT>,
            val resultTuples: MultiSet<T>,
            val resultTemplates: MultiSet<TT>
    ) : ExternalEvent<T, TT>()

    data class TemporalEvent<T, TT>(
            val primitive: TemporalPrimitive,
            val time: Long
    ) : ExternalEvent<T, TT>()

    data class SpatialEvent<T, TT>(
            val primitive: SpatialPrimitive,
            val time: Long
    ) : ExternalEvent<T, TT>()
}