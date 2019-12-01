package it.unibo.coordination.linda.text;

import it.unibo.coordination.Engines;
import it.unibo.coordination.linda.core.InspectableTupleSpace;

import java.util.concurrent.ExecutorService;

public interface InspectableTextualSpace extends TextualSpace, InspectableTupleSpace<StringTuple, RegexTemplate, Object, String, RegularMatch> {

    static InspectableTextualSpace local(String name, ExecutorService executorService) {
        return new TextualSpaceImpl(name, executorService);
    }

    static InspectableTextualSpace local(String name) {
        return local(name, Engines.getDefaultEngine());
    }

    static InspectableTextualSpace local(ExecutorService executorService) {
        return local(null, executorService);
    }

    static InspectableTextualSpace local() {
        return local(null, Engines.getDefaultEngine());
    }

}
