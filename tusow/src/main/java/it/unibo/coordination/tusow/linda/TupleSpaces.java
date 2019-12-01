package it.unibo.coordination.tusow.linda;

import it.unibo.coordination.linda.logic.LogicSpace;
import it.unibo.coordination.linda.text.TextualSpace;

import java.util.HashMap;
import java.util.Map;

public final class TupleSpaces {

    private TupleSpaces() {}

    private static Map<String, LogicSpace> logicSpaces = new HashMap<>();
    private static Map<String, TextualSpace> textualSpaces = new HashMap<>();

    public static LogicSpace getLogicSpace(String name) {
        if (!logicSpaces.containsKey(name)) {
            logicSpaces.put(name, LogicSpace.local(name));
        }
        return logicSpaces.get(name);
    }

    public static TextualSpace getTextualSpace(String name) {
        if (!textualSpaces.containsKey(name)) {
            textualSpaces.put(name, TextualSpace.local(name));
        }
        return textualSpaces.get(name);
    }
}
