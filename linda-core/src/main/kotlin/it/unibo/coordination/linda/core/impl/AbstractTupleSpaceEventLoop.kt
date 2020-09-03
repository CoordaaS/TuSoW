package it.unibo.coordination.linda.core.impl

import it.unibo.coordination.Promise
import it.unibo.coordination.control.impl.AbstractEventLoop
import it.unibo.coordination.linda.core.*
import it.unibo.coordination.linda.core.events.OperationEvent
import it.unibo.coordination.linda.core.traits.Inspectability
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

abstract class AbstractTupleSpaceEventLoop<T : Tuple<T>, TT : Template<T>, K, V, M : Match<T, TT, K, V>>
@JvmOverloads
constructor(
        override val name: String = "${AbstractTupleSpaceEventLoop::class.simpleName}-${UUID.randomUUID()})",
        private val emitters: InspectabilityEmitters<T, TT> = InspectabilityEmitters(),
        private val callbacks: TupleSpaceInteralCallbacks<T, TT> = InspectabilityCallbacks(name, emitters)
) : InspectableTupleSpace<T, TT, K, V, M>,
        TupleSpaceImplementor<T, TT, K, V, M>,
        Inspectability<T, TT> by emitters,
        TupleSpaceInteralCallbacks<T, TT> by callbacks,
        AbstractEventLoop<TupleSpaceInternalEvent<T, TT>>() {

    companion object {
        @JvmStatic
        private val LOGGER: Logger = LoggerFactory.getLogger(this::class.java.name)
    }

    @Suppress("MemberVisibilityCanBePrivate")
    protected fun log(format: String, vararg args: Any) {
        if (LOGGER.isInfoEnabled) {
            LOGGER.info(String.format("[$name] $format", *args))
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as AbstractTupleSpaceEventLoop<*, *, *, *, *>?
        return name == that!!.name
    }

    override fun hashCode(): Int {
        return Objects.hash(name)
    }

    override fun readAll(template: TT): Promise<Collection<M>> =
            generateInvocationEventAsynchronously(
                    OperationEvent.templateAcceptingInvocation(name, OperationType.READ_ALL, template)
            )

    override fun takeAll(template: TT): Promise<Collection<M>> =
            generateInvocationEventAsynchronously(
                    OperationEvent.templateAcceptingInvocation(name, OperationType.TAKE_ALL, template)
            )

    override fun writeAll(tuples: Collection<T>): Promise<Collection<T>> =
            generateInvocationEventAsynchronously(
                    OperationEvent.tuplesAcceptingInvocation(name, OperationType.READ_ALL, tuples)
            )

    private fun <X> generateInvocationEventAsynchronously(event: OperationEvent.Invocation<T, TT>): Promise<X> {
        emitters.operationEventEmitter.syncEmit(event)
        log(event.toLogMessage())
        return Promise<X>().also { schedule(TupleSpaceInternalEvent(event, it)) }
    }

    override fun read(template: TT): Promise<M> =
            generateInvocationEventAsynchronously(
                    OperationEvent.templateAcceptingInvocation(name, OperationType.READ, template)
            )

    override fun take(template: TT): Promise<M> =
            generateInvocationEventAsynchronously(
                    OperationEvent.templateAcceptingInvocation(name, OperationType.TAKE, template)
            )

    override fun write(tuple: T): Promise<T> =
            generateInvocationEventAsynchronously(
                    OperationEvent.tupleAcceptingInvocation(name, OperationType.WRITE, tuple)
            )

    override fun get(): Promise<Collection<T>> =
            generateInvocationEventAsynchronously(
                    OperationEvent.nothingAcceptingInvocation(name, OperationType.GET)
            )

    override fun getSize(): Promise<Int> =
            generateInvocationEventAsynchronously(
                    OperationEvent.nothingAcceptingInvocation(name, OperationType.GET_SIZE)
            )

    override fun tryTake(template: TT): Promise<M> =
            generateInvocationEventAsynchronously(
                    OperationEvent.templateAcceptingInvocation(name, OperationType.TRY_TAKE, template)
            )

    override fun tryRead(template: TT): Promise<M> =
            generateInvocationEventAsynchronously(
                    OperationEvent.templateAcceptingInvocation(name, OperationType.TRY_READ, template)
            )

    override fun absent(template: TT): Promise<M> =
            generateInvocationEventAsynchronously(
                    OperationEvent.templateAcceptingInvocation(name, OperationType.ABSENT, template)
            )

    override fun tryAbsent(template: TT): Promise<M> =
            generateInvocationEventAsynchronously(
                    OperationEvent.templateAcceptingInvocation(name, OperationType.TRY_ABSENT, template)
            )

    override fun getAllPendingRequests(): Promise<Collection<PendingRequest<T, TT>>> =
            generateInvocationEventAsynchronously(
                    OperationEvent.nothingAcceptingInvocation(name, OperationType.GET_SIZE)
            )

    override fun onEvent(event: TupleSpaceInternalEvent<T, TT>) {
        when (event.cause) {
            is OperationEvent.Invocation<T, TT> -> TODO()
            else -> TODO()
        }
    }
}