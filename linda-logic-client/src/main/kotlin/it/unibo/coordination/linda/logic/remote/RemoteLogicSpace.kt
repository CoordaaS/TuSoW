package it.unibo.coordination.linda.logic.remote

import alice.tuprolog.Term
import it.unibo.coordination.linda.logic.LogicMatch
import it.unibo.coordination.linda.logic.LogicSpace
import it.unibo.coordination.linda.logic.LogicTemplate
import it.unibo.coordination.linda.logic.LogicTuple
import it.unibo.coordination.linda.remote.RemoteTupleSpace
import java.net.URL

interface RemoteLogicSpace : LogicSpace, RemoteTupleSpace<LogicTuple, LogicTemplate, String, Term, LogicMatch> {
    companion object {
        @JvmStatic fun of(address: String = "localhost", port: Int = 8080, name: String = "default"): RemoteLogicSpace {
            return of(URL("http", address, port, ""), name)
        }

        @JvmStatic fun of(url: URL, name: String = "default"): RemoteLogicSpace {
            return RemoteLogicSpaceImpl(url, name)
        }
    }
}

fun LogicSpace.remoteOf(address: String = "localhost", port: Int = 8080, name: String = "default"): RemoteLogicSpace {
    return RemoteLogicSpace.of(address, port, name)
}

fun LogicSpace.remoteOf(url: URL, name: String = "default"): RemoteLogicSpace {
    return RemoteLogicSpace.of(url, name)
}