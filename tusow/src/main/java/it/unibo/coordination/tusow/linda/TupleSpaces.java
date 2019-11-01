package it.unibo.coordination.tusow.linda;

import it.unibo.coordination.linda.logic.LogicSpace;
import it.unibo.coordination.linda.text.StringSpace;

import java.util.HashMap;
import java.util.Map;

public final class TupleSpaces {

    private TupleSpaces() {}

    private static Map<String, LogicSpace> logicSpaces = new HashMap<>();
    private static Map<String, StringSpace> textualSpaces = new HashMap<>();

    public static LogicSpace getLogicSpace(String name) {
        if (!logicSpaces.containsKey(name)) {
            logicSpaces.put(name, LogicSpace.deterministic(name));
        }
        return logicSpaces.get(name);
    }

    public static StringSpace getTextualSpace(String name) {
        if (!textualSpaces.containsKey(name)) {
            textualSpaces.put(name, StringSpace.deterministic(name));
        }
        return textualSpaces.get(name);
    }
}
