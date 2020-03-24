package it.unibo.coordination.tusow.api

import io.vertx.core.Promise
import io.vertx.ext.web.RoutingContext
import it.unibo.coordination.linda.core.Match
import it.unibo.coordination.linda.core.Template
import it.unibo.coordination.linda.core.Tuple
import it.unibo.coordination.linda.core.TupleSpace
import it.unibo.coordination.tusow.exceptions.BadContentError
import it.unibo.coordination.tusow.exceptions.NotImplementedError
import java.util.function.Consumer

internal abstract class AbstractTupleSpaceApi<T : Tuple<T>, TT : Template<T>, K, V, M : Match<T, TT, K, V>, TS : TupleSpace<T, TT, K, V, M>>(routingContext: RoutingContext) : AbstractApi(routingContext), TupleSpaceApi<T, TT, K, V, M> {
    
    protected abstract fun getTupleSpaceByName(name: String): TS
    
    override fun createNewTuples(tupleSpaceName: String, bulk: Boolean, tuples: Collection<T>, promise: Promise<Collection<T>>) {
        val tupleSpace = getTupleSpaceByName(tupleSpaceName)
        if (bulk) {
            tupleSpace.writeAll(tuples).thenAcceptAsync { promise.complete(it) }
        } else {
            if (tuples.isEmpty()) {
                throw BadContentError()
            }
            tupleSpace.write(tuples.first()).thenAcceptAsync { promise.complete(listOf(it)) }
        }
    }

    override fun observeTuples(tupleSpaceName: String, bulk: Boolean, predicative: Boolean, negated: Boolean, template: TT, promise: Promise<Collection<M>>) {
        val tupleSpace = getTupleSpaceByName(tupleSpaceName)
        if (bulk && predicative) throw BadContentError()
        if (negated) {
            if (bulk) {
                throw NotImplementedError()
            } else if (predicative) {
                tupleSpace.tryAbsent(template).thenAcceptAsync(promise.singleMatchHandler())
            } else {
                tupleSpace.absent(template).thenAcceptAsync(promise.singleMatchHandler())
            }
        } else {
            if (bulk) {
                tupleSpace.readAll(template).thenAcceptAsync(promise.multipleMatchesHandler())
            } else if (predicative) {
                tupleSpace.tryRead(template).thenAcceptAsync(promise.singleMatchHandler())
            } else {
                tupleSpace.read(template).thenAcceptAsync(promise.singleMatchHandler())
            }
        }
    }

    override fun consumeTuples(tupleSpaceName: String, bulk: Boolean, predicative: Boolean, template: TT, promise: Promise<Collection<M>>) {
        val tupleSpace = getTupleSpaceByName(tupleSpaceName)
        if (bulk && predicative) throw BadContentError()
        if (bulk) {
            tupleSpace.takeAll(template).thenAcceptAsync(promise.multipleMatchesHandler())
        } else if (predicative) {
            tupleSpace.tryTake(template).thenAcceptAsync(promise.singleMatchHandler())
        } else {
            tupleSpace.take(template).thenAcceptAsync(promise.singleMatchHandler())
        }
    }

    protected abstract fun ensureCorrectTypeForMatch(match: Match<T, TT, K, V>): M
    
    private fun Promise<Collection<M>>.singleMatchHandler(): Consumer<Match<T, TT, K, V>> {
        return Consumer { complete(listOf(ensureCorrectTypeForMatch(it))) }
    }

    private fun Promise<Collection<M>>.multipleMatchesHandler(): Consumer<Collection<Match<T, TT, K, V>>> {
        return Consumer { complete(it.map(this@AbstractTupleSpaceApi::ensureCorrectTypeForMatch)) }
    }

    override fun getAllTuples(tupleSpaceName: String, promise: Promise<Collection<T>>) {
        getTupleSpaceByName(tupleSpaceName)
                .get()
                .thenAcceptAsync { promise.complete(it) }
    }

    override fun countTuples(tupleSpaceName: String, promise: Promise<Int>) {
        getTupleSpaceByName(tupleSpaceName)
                .getSize()
                .thenAcceptAsync { promise.complete(it) }
    }
}