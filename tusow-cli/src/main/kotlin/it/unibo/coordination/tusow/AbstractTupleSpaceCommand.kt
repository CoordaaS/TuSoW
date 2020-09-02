package it.unibo.coordination.tusow

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.enum
import com.github.ajalt.clikt.parameters.types.int
import it.unibo.coordination.linda.core.TupleSpace
import it.unibo.coordination.linda.logic.remote.RemoteLogicSpace
import it.unibo.coordination.linda.text.remote.RemoteTextualSpace
import it.unibo.coordination.tusow.TupleSpaceTypes.LOGIC
import it.unibo.coordination.tusow.TupleSpaceTypes.TEXT
import java.net.URL
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutionException

abstract class AbstractTupleSpaceCommand(
        help: String = "",
        epilog: String = "",
        name: String? = null,
        invokeWithoutSubcommand: Boolean = false,
        printHelpOnEmptyArgs: Boolean = false,
        helpTags: Map<String, String> = emptyMap(),
        autoCompleteEnvvar: String? = ""
) : CliktCommand(help, epilog, name, invokeWithoutSubcommand, printHelpOnEmptyArgs, helpTags, autoCompleteEnvvar) {

    val tupleSpaceName: String by option(
            "--tuplespace", "-T",
            help = "The tuple space name this operation should be invoked upon. Defaults to 'default'"
    ).default("default")

    val host: String by option(
            "--host", "-h",
            help = "The name of the server hosting the tuple space. Defaults to 'localhost'"
    ).default("localhost")

    val port: Int by option(
            "--port", "-P",
            help = "The port on which the server hosting the tuple space is listening upon. Defaults to 8080"
    ).int().default(8080)

    private val urlString: String? by option(
            "--url", "-u",
            help = "Alternative way of specifying the host name and port. Defaults to 'http://localhost:8080'"
    )

    val url: URL by lazy {
        urlString?.let { URL(it) } ?: URL("http", host, port, "")
    }

    val type: TupleSpaceTypes by option(
            "--type", "-t",
            help = "The type of the tuple space this operation should act upon. It can be either 'TEXT' (for textual tuple spaces) " +
                    "or 'LOGIC' (for logic tuple spaces). This also dictates the format of tuples and templates. " +
                    "Defaults to 'LOGIC'"
    ).enum<TupleSpaceTypes>().default(LOGIC)

    val tupleSpaceID: TupleSpaceID by lazy { TupleSpaceID(tupleSpaceName, type, url) }

    val bulk: Boolean by option(
            "-b", "--bulk", "-a", "--all",
            help = "Makes this operation accept (in case of a writing operation) or return (in case of an observing operation) " +
                    "more than one tuple. In other words, makes this operation a bulk operation. " +
                    "Defaults to false"
    ).flag(default = false)

    protected fun <X, R> CompletableFuture<X>.await(f: X.() -> R) {
        try {
            get().f()
        } catch (e: ExecutionException) {
            if (e.cause != null) {
                throw e.cause!!
            }
            throw e
        }
    }

    companion object {
        protected val TUPLE_SPACES: MutableMap<TupleSpaceID, TupleSpace<*, *, *, *, *>> = mutableMapOf()

        fun <T : TupleSpace<*, *, *, *, *>> getTupleSpace(id: TupleSpaceID): T {
            if (id !in TUPLE_SPACES) {
                TUPLE_SPACES[id] = when (id.type) {
                    LOGIC -> RemoteLogicSpace.of(id.host, id.name)
                    TEXT -> RemoteTextualSpace.of(id.host, id.name)
                }
            }

            @Suppress("UNCHECKED_CAST")
            return TUPLE_SPACES[id] as T
        }
    }
}