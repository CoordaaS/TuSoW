package it.unibo.coordination.linda.cli

import it.unibo.coordination.linda.cli.TupleSpaceTypes.LOGIC
import it.unibo.coordination.linda.cli.TupleSpaceTypes.TEXT
import it.unibo.coordination.linda.core.Match
import it.unibo.coordination.linda.core.Tuple
import it.unibo.coordination.linda.logic.LogicSpace
import it.unibo.coordination.linda.text.StringSpace

class AbsentCommand(
        help: String = "",
        epilog: String = "",
        name: String? = "absent",
        invokeWithoutSubcommand: Boolean = false,
        printHelpOnEmptyArgs: Boolean = false,
        helpTags: Map<String, String> = emptyMap(),
        autoCompleteEnvvar: String? = ""
) : AbstractObserveCommand(help, epilog, name, invokeWithoutSubcommand, printHelpOnEmptyArgs, helpTags, autoCompleteEnvvar)  {

    override fun run() {
        when {
            bulk -> TODO("Currently not supported operation")
            predicative -> when (type) {
                LOGIC -> getTupleSpace<LogicSpace>(tupleSpaceID)
                        .tryAbsent(template)
                        .defaultHandlerForSingleResult()
                TEXT -> getTupleSpace<StringSpace>(tupleSpaceID)
                        .tryAbsent(template)
                        .defaultHandlerForSingleResult()
            }
            else -> when (type) {
                LOGIC -> getTupleSpace<LogicSpace>(tupleSpaceID)
                        .absent(template)
                        .defaultHandlerForSingleResult()
                TEXT -> getTupleSpace<StringSpace>(tupleSpaceID)
                        .absent(template)
                        .defaultHandlerForSingleResult()
            }
        }
    }

    override fun <T : Tuple, TT, K, V, M : Match<T, TT, K, V>> M.isSuccess(): Boolean = !this.isMatching

    override fun <T : Tuple, TT, K, V, M : Match<T, TT, K, V>> M.getResult(): Any =
            if (isMatching) tuple.get().value else template as Any

    override fun <T : Tuple, TT, K, V, M : Match<T, TT, K, V>, C : Collection<M>> C.isSuccess(): Boolean = isEmpty()
}