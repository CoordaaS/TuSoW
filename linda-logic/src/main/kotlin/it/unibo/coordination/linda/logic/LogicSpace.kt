package it.unibo.coordination.linda.logic

import alice.tuprolog.Term
import it.unibo.coordination.Engines
import it.unibo.coordination.linda.core.TupleSpace
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService

interface LogicSpace : TupleSpace<LogicTuple, LogicTemplate, String, Term, LogicMatch> {

    @JvmDefault
    fun write(tuple: Term): CompletableFuture<LogicTuple> {
        return write(LogicTuple.of(tuple))
    }

    @JvmDefault
    fun readTuple(template: Term): CompletableFuture<LogicTuple> {
        return readTuple(LogicTemplate.of(template))
    }

    @JvmDefault
    fun tryRead(template: Term): CompletableFuture<LogicMatch> {
        return tryRead(LogicTemplate.of(template))
    }

    @JvmDefault
    fun tryAbsent(template: Term): CompletableFuture<LogicMatch> {
        return tryAbsent(LogicTemplate.of(template))
    }

    @JvmDefault
    fun tryTake(template: Term): CompletableFuture<LogicMatch> {
        return tryTake(LogicTemplate.of(template))
    }

    @JvmDefault
    fun read(template: Term): CompletableFuture<LogicMatch> {
        return read(LogicTemplate.of(template))
    }

    @JvmDefault
    fun tryReadTuple(template: Term): CompletableFuture<Optional<LogicTuple>> {
        return tryReadTuple(LogicTemplate.of(template))
    }

    @JvmDefault
    fun takeTuple(template: Term): CompletableFuture<LogicTuple> {
        return takeTuple(LogicTemplate.of(template))
    }

    @JvmDefault
    fun take(template: Term): CompletableFuture<LogicMatch> {
        return take(LogicTemplate.of(template))
    }

    @JvmDefault
    fun tryTakeTuple(template: Term): CompletableFuture<Optional<LogicTuple>> {
        return tryTakeTuple(LogicTemplate.of(template))
    }

    @JvmDefault
    fun absent(template: Term): CompletableFuture<LogicMatch> {
        return absent(LogicTemplate.of(template))
    }

    @JvmDefault
    fun tryAbsentTuple(template: Term): CompletableFuture<Optional<LogicTuple>> {
        return tryAbsentTuple(LogicTemplate.of(template))
    }

    @JvmDefault
    override fun String.toTuple(): LogicTuple = LogicTuple.of(this)

    @JvmDefault
    override fun String.toTemplate(): LogicTemplate = LogicTemplate.of(this)

    companion object {

        @JvmStatic
        fun local(name: String?, executorService: ExecutorService): LogicSpace {
            return LogicSpaceImpl(name, executorService)
        }

        @JvmStatic
        fun local(name: String?): LogicSpace {
            return local(name, Engines.defaultEngine)
        }

        @JvmStatic
        fun local(executorService: ExecutorService): LogicSpace {
            return local(null, executorService)
        }

        @JvmStatic
        fun local(): LogicSpace {
            return local(null, Engines.defaultEngine)
        }
    }
}
