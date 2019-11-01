package it.unibo.coordination.linda.text;

import it.unibo.coordination.Engines;
import it.unibo.coordination.linda.core.InspectableTupleSpace;

import java.util.concurrent.ExecutorService;

public interface InspectableStringSpace extends StringSpace, InspectableTupleSpace<StringTuple, RegexTemplate, Object, String, RegularMatch> {

    static InspectableStringSpace deterministic(String name, ExecutorService executorService) {
        return new DeterministicStringSpace(name, executorService);
    }

    static InspectableStringSpace deterministic(String name) {
        return deterministic(name, Engines.getDefaultEngine());
    }

    static InspectableStringSpace deterministic(ExecutorService executorService) {
        return deterministic(null, Engines.getDefaultEngine());
    }

}
