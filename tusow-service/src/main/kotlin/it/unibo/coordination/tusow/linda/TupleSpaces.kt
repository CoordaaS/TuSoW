package it.unibo.coordination.tusow.linda

import it.unibo.coordination.linda.logic.LogicSpace
import it.unibo.coordination.linda.text.TextualSpace
import java.util.*

object TupleSpaces {

    private val logicSpaces: MutableMap<String, LogicSpace> = HashMap()

    private val textualSpaces: MutableMap<String, TextualSpace> = HashMap()

    @JvmStatic
    fun getLogicSpace(name: String): LogicSpace {
        if (!logicSpaces.containsKey(name)) {
            logicSpaces[name] = LogicSpace.local(name)
        }
        return logicSpaces[name]!!
    }

    @JvmStatic
    fun getTextualSpace(name: String): TextualSpace {
        if (!textualSpaces.containsKey(name)) {
            textualSpaces[name] = TextualSpace.local(name)
        }
        return textualSpaces[name]!!
    }
}