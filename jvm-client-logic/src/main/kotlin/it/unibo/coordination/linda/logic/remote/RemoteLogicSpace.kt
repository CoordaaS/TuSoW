package it.unibo.coordination.linda.logic.remote

import it.unibo.coordination.linda.logic.LogicSpace
import java.net.URL

interface RemoteLogicSpace : LogicSpace {
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