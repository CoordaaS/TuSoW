package it.unibo.coordination.linda.core.impl

import it.unibo.coordination.linda.core.Match
import it.unibo.coordination.linda.core.PendingRequest
import it.unibo.coordination.linda.core.Template
import it.unibo.coordination.linda.core.Tuple
import java.util.stream.Stream

interface TupleSpaceImplementor<T : Tuple<T>, TT : Template<T>, K, V, M : Match<T, TT, K, V>> {

    val pendingRequests: MutableCollection<LocalPendingRequest<T, TT, M>>

    val pendingRequestsIterator: MutableIterator<LocalPendingRequest<T, TT, M>>
        get() = pendingRequests.iterator()

    val allTuples: Stream<T>

    fun lookForTuple(template: TT): M

    fun lookForTuples(template: TT, limit: Int): Stream<out M>

    fun lookForTuples(template: TT): Stream<out M> {
        return lookForTuples(template, Integer.MAX_VALUE)
    }

    fun retrieveTuples(template: TT, limit: Int): Stream<out M>

    fun retrieveTuple(template: TT): M

    fun retrieveTuples(template: TT): Stream<out M> {
        return retrieveTuples(template, Integer.MAX_VALUE)
    }

    fun match(template: TT, tuple: T): M

    fun failedMatch(template: TT): M

    fun insertTuple(tuple: T)

    fun countTuples(): Int

    fun onSuspending(request: PendingRequest<T, TT>)

    fun onResuming(request: PendingRequest<T, TT>)

    fun onTaking(tuple: T)

    fun onTaken(tuple: T)

    fun onReading(tuple: T)

    fun onRead(tuple: T)

    fun onWriting(tuple: T)

    fun onWritten(tuple: T)

    fun onMissing(template: TT)

    fun onMissing(template: TT, counterExample: T)

    fun onMissed(template: TT)

    fun onMissed(template: TT, counterExample: T)
}