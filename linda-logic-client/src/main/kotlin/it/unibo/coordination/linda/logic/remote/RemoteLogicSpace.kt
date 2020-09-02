package it.unibo.coordination.linda.logic.remote

import it.unibo.coordination.linda.logic.LogicMatch
import it.unibo.coordination.linda.logic.LogicSpace
import it.unibo.coordination.linda.logic.LogicTemplate
import it.unibo.coordination.linda.logic.LogicTuple
import it.unibo.coordination.linda.remote.RemoteTupleSpace
import it.unibo.tuprolog.core.Term
import java.net.URL

interface RemoteLogicSpace : LogicSpace, RemoteTupleSpace<LogicTuple, LogicTemplate, String, Term, LogicMatch> {
    companion object {
        @JvmStatic
        fun of(address: String, port: Int, name: String): RemoteLogicSpace {
            return of(URL("http", address, port, ""), name)
        }

        @JvmStatic
        fun of(address: String, name: String): RemoteLogicSpace {
            return of(address, 8080, name)
        }

        @JvmStatic
        fun of(name: String): RemoteLogicSpace {
            return of("localhost", name)
        }

        @JvmStatic
        fun of(): RemoteLogicSpace {
            return of("default")
        }

        @JvmStatic
        fun of(port: Int, name: String): RemoteLogicSpace {
            return of("localhost", port, name)
        }

        @JvmStatic
        fun of(port: Int): RemoteLogicSpace {
            return of(port, "default")
        }

        @JvmStatic
        fun of(address: String, port: Int): RemoteLogicSpace {
            return of(address, port, "default")
        }

        @JvmStatic
        fun of(url: URL, name: String): RemoteLogicSpace {
            return RemoteLogicSpaceImpl(url, name)
        }

        @JvmStatic
        fun of(url: URL): RemoteLogicSpace {
            return of(url, "default")
        }
    }
}

fun LogicSpace.remote(address: String = "localhost", port: Int = 8080, name: String = "default"): RemoteLogicSpace {
    return RemoteLogicSpace.of(address, port, name)
}

fun LogicSpace.remote(url: URL, name: String = "default"): RemoteLogicSpace {
    return RemoteLogicSpace.of(url, name)
}