package it.unibo.coordination.tusow.api

import io.vertx.core.Promise
import it.unibo.coordination.linda.core.Match
import it.unibo.coordination.linda.core.Template
import it.unibo.coordination.linda.core.Tuple

interface TupleSpaceApi<T : Tuple<T>, TT : Template<T>, K, V, M : Match<T, TT, K, V>> : Api {
    fun createNewTuples(tupleSpaceName: String, bulk: Boolean, tuples: Collection<T>, promise: Promise<Collection<T>>)
    fun observeTuples(tupleSpaceName: String, bulk: Boolean, predicative: Boolean, negated: Boolean, template: TT, promise: Promise<Collection<M>>)
    fun consumeTuples(tupleSpaceName: String, bulk: Boolean, predicative: Boolean, template: TT, promise: Promise<Collection<M>>)
    fun getAllTuples(tupleSpaceName: String, promise: Promise<Collection<T>>)
    fun countTuples(tupleSpaceName: String, promise: Promise<Int>)
}