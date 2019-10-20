package it.unibo.coordination.linda.strings.remote

import it.unibo.coordination.linda.string.StringSpace
import java.net.URL

interface RemoteStringSpace : StringSpace {
    companion object {
        @JvmStatic fun of(address: String = "localhost", port: Int = 8080, name: String = "default"): RemoteStringSpace {
            return of(URL("http", address, port, ""), name)
        }

        @JvmStatic fun of(url: URL, name: String = "default"): RemoteStringSpace {
            return RemoteStringSpaceImpl(url, name)
        }
    }
}

fun StringSpace.remoteOf(address: String = "localhost", port: Int = 8080, name: String = "default"): RemoteStringSpace {
    return RemoteStringSpace.of(address, port, name)
}

fun StringSpace.remoteOf(url: URL, name: String = "default"): RemoteStringSpace {
    return RemoteStringSpace.of(url, name)
}