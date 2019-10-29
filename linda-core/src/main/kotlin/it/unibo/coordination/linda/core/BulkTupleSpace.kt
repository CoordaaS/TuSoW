package it.unibo.coordination.linda.core

import it.unibo.coordination.Promise
import org.apache.commons.collections4.multiset.HashMultiSet

interface BulkTupleSpace<T : Tuple, TT : Template, K, V> : TupleSpace<T, TT, K, V> {

    fun readAll(template: TT): Promise<Collection<out Match<T, TT, K, V>>>

    @JvmDefault
    fun readAllTuples(template: TT): Promise<Collection<out T>> {
        return readAll(template).thenApplyAsync { matches ->
            HashMultiSet(matches.map { it.tuple.get() })
        }
    }

    fun takeAll(template: TT): Promise<Collection<out Match<T, TT, K, V>>>

    @JvmDefault
    fun takeAllTuples(template: TT): Promise<Collection<out T>> {
        return takeAll(template).thenApplyAsync { matches ->
            HashMultiSet(matches.map { it.tuple.get() })
        }
    }

    fun writeAll(tuples: Collection<out T>): Promise<Collection<out T>>

    @JvmDefault
    fun writeAll(tuple1: T, vararg otherTuples: T): Promise<Collection<out T>> {
        return writeAll(listOf(tuple1, *otherTuples))
    }
}