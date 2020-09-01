package it.unibo.coordination.linda.logic.remote

import it.unibo.coordination.linda.logic.LogicMatch
import it.unibo.coordination.linda.logic.LogicTemplate
import it.unibo.coordination.linda.logic.LogicTuple
import it.unibo.coordination.linda.remote.AbstractRemoteTupleSpace
import it.unibo.presentation.Presentation
import it.unibo.tuprolog.core.Term
import java.net.URL

internal class RemoteLogicSpaceImpl(service: URL, name: String) : RemoteLogicSpace, AbstractRemoteTupleSpace<LogicTuple, LogicTemplate, String, Term, LogicMatch>(service, name) {
    override val tupleSpaceType: String
        get() = "logic"

    override val tupleClass: Class<LogicTuple>
        get() = LogicTuple::class.java

    override val templateClass: Class<LogicTemplate>
        get() = LogicTemplate::class.java

    override val matchClass: Class<LogicMatch>
        get() = LogicMatch::class.java

    override val presentation: Presentation
        get() = it.unibo.coordination.linda.logic.Presentation
}