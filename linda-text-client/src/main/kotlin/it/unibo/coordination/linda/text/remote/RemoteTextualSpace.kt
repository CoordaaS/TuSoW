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
        fun of(address: String , port: Int, name: String): RemoteTextualSpace {
            return of(URL("http", address, port, ""), name)
        }

        @JvmStatic
        fun of(address: String, name: String): RemoteTextualSpace {
            return of(address, 8080, name)
        }

        @JvmStatic
        fun of(name: String): RemoteTextualSpace {
            return of("localhost", name)
        }

        @JvmStatic
        fun of(): RemoteTextualSpace {
            return of("default")
        }

        @JvmStatic
        fun of(port: Int, name: String): RemoteTextualSpace {
            return of("localhost", port, name)
        }

        @JvmStatic
        fun of(port: Int): RemoteTextualSpace {
            return of(port, "default")
        }

        @JvmStatic
        fun of(address: String , port: Int): RemoteTextualSpace {
            return of(address, port, "default")
        }

        @JvmStatic
        fun of(url: URL, name: String): RemoteTextualSpace {
            return RemoteTextualSpaceImpl(url, name)
        }

        @JvmStatic
        fun of(url: URL): RemoteTextualSpace {
            return of(url, "default")
        }
    }
}

fun TextualSpace.remote(address: String = "localhost", port: Int = 8080, name: String = "default"): RemoteTextualSpace {
    return RemoteTextualSpace.of(address, port, name)
}

fun TextualSpace.remote(url: URL, name: String = "default"): RemoteTextualSpace {
    return RemoteTextualSpace.of(url, name)
}