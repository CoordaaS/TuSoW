package it.unibo.coordination.linda.text.remote

import it.unibo.coordination.linda.text.TextualSpace
import java.net.URL

interface RemoteTextualSpace : TextualSpace {
    companion object {
        @JvmStatic fun of(address: String = "localhost", port: Int = 8080, name: String = "default"): RemoteTextualSpace {
            return of(URL("http", address, port, ""), name)
        }

        @JvmStatic fun of(url: URL, name: String = "default"): RemoteTextualSpace {
            return RemoteTextualSpaceImpl(url, name)
        }
    }
}

fun TextualSpace.remoteOf(address: String = "localhost", port: Int = 8080, name: String = "default"): RemoteTextualSpace {
    return RemoteTextualSpace.of(address, port, name)
}

fun TextualSpace.remoteOf(url: URL, name: String = "default"): RemoteTextualSpace {
    return RemoteTextualSpace.of(url, name)
}