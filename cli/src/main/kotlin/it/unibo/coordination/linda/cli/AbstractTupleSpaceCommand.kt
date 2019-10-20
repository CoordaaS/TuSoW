package it.unibo.coordination.linda.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.enum
import com.github.ajalt.clikt.parameters.types.int
import it.unibo.coordination.linda.cli.TupleSpaceTypes.LOGIC
import it.unibo.coordination.linda.cli.TupleSpaceTypes.TEXT
import it.unibo.coordination.linda.core.ExtendedTupleSpace
import it.unibo.coordination.linda.core.Match
import it.unibo.coordination.linda.logic.remote.RemoteLogicSpace
import it.unibo.coordination.linda.strings.remote.RemoteStringSpace
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

    val tupleSpaceName: String by option("--tuplespace", "-ts").default("default")

    val host: String by option("--host", "-h").default("localhost")

    val port: Int by option("--port", "-p").int().default(8080)

    private val urlString: String? by option("--url", "-u")

    val url: URL by lazy {
        urlString?.let { URL(it) } ?: URL("http", host, port, "")
    }

    val type: TupleSpaceTypes by option("--type", "-t").enum<TupleSpaceTypes>().default(LOGIC)

    val tupleSpaceID: TupleSpaceID by lazy { TupleSpaceID(tupleSpaceName, type, url) }

    protected fun<T, TT, K, V, M : Match<T, TT, K, V>> CompletableFuture<M>.onSingleMatchCompletion(f: M.()->Unit): Unit {
        try {
            get().f()
        } catch (e: ExecutionException) {
            e.cause?.printStackTrace()
        }
    }

    protected fun<T, TT, K, V, M : Match<T, TT, K, V>, C : Collection<out M>> CompletableFuture<C>.onMultipleMatchCompletion(f: C.()->Unit): Unit {
        try {
            get().f()
        } catch (e: ExecutionException) {
            e.cause?.printStackTrace()
        }
    }

    companion object {
        protected val tupleSpaces: MutableMap<TupleSpaceID, ExtendedTupleSpace<*, *, *, *>> = mutableMapOf()

        fun<T : ExtendedTupleSpace<*, *, *, *>> getTupleSpace(id: TupleSpaceID): T {
            if (id !in tupleSpaces) {
                tupleSpaces[id] = when (id.type) {
                    LOGIC -> RemoteLogicSpace.of(id.host, id.name)
                    TEXT -> RemoteStringSpace.of(id.host, id.name)
                }
            }
            return tupleSpaces[id] as T
        }
    }
}