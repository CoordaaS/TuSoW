package it.unibo.coordination.linda.core

import it.unibo.coordination.Promise
import org.apache.commons.collections4.multiset.HashMultiSet

interface BulkTupleSpace<T : Tuple, TT : Template, K, V> : LindaTupleSpace<T, TT, K, V> {

    fun readAll(template: TT): Promise<Collection<Match<T, TT, K, V>>>

    @JvmDefault
    fun readAll(template: String): Promise<Collection<Match<T, TT, K, V>>> =
            readAll(template.toTemplate())

    @JvmDefault
    fun readAllTuples(template: TT): Promise<Collection<T>> {
        return readAll(template).thenApplyAsync { matches ->
            HashMultiSet(matches.map { it.tuple.get() })
        }
    }

    @JvmDefault
    fun readAllTuples(template: String): Promise<Collection<T>> =
            readAllTuples(template.toTemplate())

    fun takeAll(template: TT): Promise<Collection<Match<T, TT, K, V>>>

    @JvmDefault
    fun takeAll(template: String): Promise<Collection<Match<T, TT, K, V>>> =
            takeAll(template.toTemplate())

    @JvmDefault
    fun takeAllTuples(template: TT): Promise<Collection<T>> {
        return takeAll(template).thenApplyAsync { matches ->
            HashMultiSet(matches.map { it.tuple.get() })
        }
    }

    @JvmDefault
    fun takeAllTuples(template: String): Promise<Collection<T>> =
            takeAllTuples(template.toTemplate())

    fun writeAll(tuples: Collection<T>): Promise<Collection<T>>

    @JvmDefault
    fun writeAll(tuple1: String, vararg otherTuples: String): Promise<Collection<T>> =
            writeAll(listOf(tuple1, *otherTuples).map { it.toTuple() })

    @JvmDefault
    fun writeAll(tuple1: T, vararg otherTuples: T): Promise<Collection<T>> {
        return writeAll(listOf(tuple1, *otherTuples))
    }
}