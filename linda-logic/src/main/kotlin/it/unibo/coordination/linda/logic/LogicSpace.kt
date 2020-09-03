package it.unibo.coordination.linda.logic

import it.unibo.coordination.Engines
import it.unibo.coordination.linda.core.TupleSpace
import it.unibo.tuprolog.core.Term
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService

interface LogicSpace : TupleSpace<LogicTuple, LogicTemplate, String, Term, LogicMatch> {

    @JvmDefault
    fun write(tuple: Term): CompletableFuture<LogicTuple> = write(LogicTuple.of(tuple))

    @JvmDefault
    fun readTuple(template: Term): CompletableFuture<LogicTuple> = readTuple(LogicTemplate.of(template))

    @JvmDefault
    fun tryRead(template: Term): CompletableFuture<LogicMatch> = tryRead(LogicTemplate.of(template))

    @JvmDefault
    fun tryAbsent(template: Term): CompletableFuture<LogicMatch> = tryAbsent(LogicTemplate.of(template))

    @JvmDefault
    fun tryTake(template: Term): CompletableFuture<LogicMatch> = tryTake(LogicTemplate.of(template))

    @JvmDefault
    fun read(template: Term): CompletableFuture<LogicMatch> = read(LogicTemplate.of(template))

    @JvmDefault
    fun tryReadTuple(template: Term): CompletableFuture<Optional<LogicTuple>> = tryReadTuple(LogicTemplate.of(template))

    @JvmDefault
    fun takeTuple(template: Term): CompletableFuture<LogicTuple> = takeTuple(LogicTemplate.of(template))

    @JvmDefault
    fun take(template: Term): CompletableFuture<LogicMatch> = take(LogicTemplate.of(template))

    @JvmDefault
    fun tryTakeTuple(template: Term): CompletableFuture<Optional<LogicTuple>> = tryTakeTuple(LogicTemplate.of(template))

    @JvmDefault
    fun absent(template: Term): CompletableFuture<LogicMatch> = absent(LogicTemplate.of(template))

    @JvmDefault
    fun tryAbsentTuple(template: Term): CompletableFuture<Optional<LogicTuple>> =
            tryAbsentTuple(LogicTemplate.of(template))

    @JvmDefault
    override fun String.toTuple(): LogicTuple = LogicTuple.of(this)

    @JvmDefault
    override fun String.toTemplate(): LogicTemplate = LogicTemplate.of(this)

    companion object {

        @JvmStatic
        fun local(name: String?, executorService: ExecutorService): LogicSpace =
                when (name) {
                    null -> LogicSpaceImpl(executor = executorService)
                    else -> LogicSpaceImpl(name, executorService)
                }

        @JvmStatic
        fun local(name: String?): LogicSpace = local(name, Engines.defaultEngine)

        @JvmStatic
        fun local(executorService: ExecutorService): LogicSpace = local(null, executorService)

        @JvmStatic
        fun local(): LogicSpace = local(null, Engines.defaultEngine)
    }
}
