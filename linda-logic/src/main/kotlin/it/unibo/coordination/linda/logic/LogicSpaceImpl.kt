package it.unibo.coordination.linda.logic

import java.util.*
import java.util.concurrent.ExecutorService

internal class LogicSpaceImpl(
        name: String = "${LogicSpaceImpl::class.simpleName}-${UUID.randomUUID()}",
        executor: ExecutorService
) : AbstractLogicSpaceImpl(name, executor), InspectableLogicSpace {

    private val pendingQueue = LinkedList<LogicPendingRequest>()

    override val pendingRequests: MutableCollection<LogicPendingRequest>
        get() = pendingQueue


}
