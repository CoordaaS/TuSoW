package it.unibo.coordination.linda.logic.remote

import alice.tuprolog.Term
import it.unibo.coordination.linda.logic.LogicMatch
import it.unibo.coordination.linda.logic.LogicTemplate
import it.unibo.coordination.linda.logic.LogicTuple
import it.unibo.coordination.linda.remote.AbstractRemoteTupleSpace
import java.net.URL

internal class RemoteLogicSpaceImpl(service: URL, name: String) : RemoteLogicSpace, AbstractRemoteTupleSpace<LogicTuple, LogicTemplate, String, Term, LogicMatch>(service, name) {
    override val tupleClass: Class<LogicTuple>
        get() = LogicTuple::class.java

    override val templateClass: Class<LogicTemplate>
        get() = LogicTemplate::class.java

    override val matchClass: Class<LogicMatch>
        get() = LogicMatch::class.java
}