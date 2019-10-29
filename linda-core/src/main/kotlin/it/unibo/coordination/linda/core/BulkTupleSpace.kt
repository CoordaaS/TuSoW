package it.unibo.coordination.linda.core

import it.unibo.coordination.Promise
import org.apache.commons.collections4.MultiSet
import org.apache.commons.collections4.multiset.HashMultiSet
import java.util.concurrent.Future

interface BulkTupleSpace<T : Tuple, TT : Template, K, V> : TupleSpace<T, TT, K, V> {

    fun readAll(template: TT): Promise<Collection<Match<T, TT, K, V>>>

    fun readAllTuples(template: TT): Promise<MultiSet<T>> {
        return readAll(template).thenApplyAsync { matches ->
            HashMultiSet(matches.map { it.tuple.get() })
        }
    }

    fun takeAll(template: TT): Promise<Collection<Match<T, TT, K, V>>>

    fun takeAllTuples(template: TT): Promise<MultiSet<T>> {
        return takeAll(template).thenApplyAsync { matches ->
            HashMultiSet(matches.map { it.tuple.get() })
        }
    }

    fun writeAll(tuples: Collection<T>): Promise<MultiSet<T>>

    fun writeAll(tuple1: T, vararg otherTuples: T): Future<MultiSet<T>> {
        return writeAll(listOf(tuple1, *otherTuples))
    }
}