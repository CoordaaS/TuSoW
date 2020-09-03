package it.unibo.coordination.linda.core.impl

import it.unibo.coordination.linda.core.PendingRequest
import it.unibo.coordination.linda.core.Template
import it.unibo.coordination.linda.core.Tuple

interface TupleSpaceInteralCallbacks<T : Tuple<T>, TT : Template<T>> {

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