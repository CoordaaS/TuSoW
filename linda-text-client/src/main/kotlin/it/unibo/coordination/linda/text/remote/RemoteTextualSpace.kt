package it.unibo.coordination.linda.text.remote

import it.unibo.coordination.linda.remote.RemoteTupleSpace
import it.unibo.coordination.linda.text.RegexTemplate
import it.unibo.coordination.linda.text.RegularMatch
import it.unibo.coordination.linda.text.StringTuple
import it.unibo.coordination.linda.text.TextualSpace
import java.net.URL

interface RemoteTextualSpace : TextualSpace, RemoteTupleSpace<StringTuple, RegexTemplate, Any, String, RegularMatch> {
    companion object {
        @JvmStatic
        @JvmOverloads
        fun of(address: String = "localhost", port: Int = 8080, name: String = "default"): RemoteTextualSpace {
            return of(URL("http", address, port, ""), name)
        }

        @JvmStatic
        @JvmOverloads
        fun of(url: URL, name: String = "default"): RemoteTextualSpace {
            return RemoteTextualSpaceImpl(url, name)
        }
    }
}

@JvmOverloads
fun TextualSpace.remote(address: String = "localhost", port: Int = 8080, name: String = "default"): RemoteTextualSpace {
    return RemoteTextualSpace.of(address, port, name)
}

@JvmOverloads
fun TextualSpace.remote(url: URL, name: String = "default"): RemoteTextualSpace {
    return RemoteTextualSpace.of(url, name)
}