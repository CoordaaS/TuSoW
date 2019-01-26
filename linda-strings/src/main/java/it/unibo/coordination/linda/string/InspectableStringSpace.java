package it.unibo.coordination.linda.string;

import it.unibo.coordination.Engine;
import it.unibo.coordination.linda.core.InspectableExtendedTupleSpace;

import java.util.concurrent.ExecutorService;

public interface InspectableStringSpace extends StringSpace, InspectableExtendedTupleSpace<StringTuple, RegexTemplate, Object, String> {

    static InspectableStringSpace deterministic(String name, ExecutorService executorService) {
        return new DeterministicStringSpace(name, executorService);
    }

    static InspectableStringSpace deterministic(String name) {
        return deterministic(name, Engine.getDefaultEngine());
    }

    static InspectableStringSpace deterministic(ExecutorService executorService) {
        return deterministic(null, Engine.getDefaultEngine());
    }

}
