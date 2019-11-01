package it.unibo.coordination.linda.text;

import it.unibo.coordination.Engines;
import it.unibo.coordination.linda.core.InspectableTupleSpace;

import java.util.concurrent.ExecutorService;

public interface InspectableTextualSpace extends TextualSpace, InspectableTupleSpace<StringTuple, RegexTemplate, Object, String, RegularMatch> {

    static InspectableTextualSpace deterministic(String name, ExecutorService executorService) {
        return new DeterministicTextualSpace(name, executorService);
    }

    static InspectableTextualSpace deterministic(String name) {
        return deterministic(name, Engines.getDefaultEngine());
    }

    static InspectableTextualSpace deterministic(ExecutorService executorService) {
        return deterministic(null, Engines.getDefaultEngine());
    }

}
