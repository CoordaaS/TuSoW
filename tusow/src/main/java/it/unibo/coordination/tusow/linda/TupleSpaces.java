package it.unibo.coordination.tusow.linda;

import it.unibo.coordination.linda.logic.LogicSpace;

import java.util.HashMap;
import java.util.Map;

public final class TupleSpaces {

    private TupleSpaces() {}

    private static Map<String, LogicSpace> logicSpaces = new HashMap<>();

    public static LogicSpace getLogicSpace(String name) {
        if (!logicSpaces.containsKey(name)) {
            logicSpaces.put(name, LogicSpace.deterministic(name));
        }
        return logicSpaces.get(name);
    }
}
