package it.unibo.coordination.linda.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.unibo.coordination.linda.core.*;
import it.unibo.coordination.linda.core.events.OperationEvent;
import it.unibo.coordination.linda.core.events.TupleEvent;
import it.unibo.coordination.linda.core.events.TupleSpaceEvent;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

abstract class TupleSpaceEventDeserializer<T extends Tuple, TT extends Template> extends DynamicDeserializer<TupleSpaceEvent> {

    private Class<T> tupleClass;
    private Class<TT> templateClass;

    protected abstract TupleSpace<T, TT, ?, ?> getSpace(String tupleSpace);

    public TupleSpaceEventDeserializer(MIMETypes mimeType, ObjectMapper mapper, Class<T> tupleClass, Class<TT> templateClass) {
        super(TupleSpaceEvent.class, mimeType, mapper);
        this.tupleClass = tupleClass;
        this.templateClass = templateClass;
    }

    @Override
    public TupleSpaceEvent fromDynamicObject(Object dynamicObject) {
        if(dynamicObject instanceof List) {
            if(((List) dynamicObject).size() == 1) {
                dynamicObject = ((List) dynamicObject).get(0);
            }
        }
        if (dynamicObject instanceof Map) {
            final var dynamicMap = (Map<String, ?>) dynamicObject;

            if (dynamicMap.containsKey("eventType") && dynamicMap.containsKey("tupleSpace")) {
                final TupleSpace<T, TT, ?, ?> tupleSpace = getSpace((String) dynamicMap.get("tupleSpace"));
                if(dynamicMap.get("eventType").equals(OperationEvent.class.getSimpleName())) {
                    if(dynamicMap.containsKey("operationPhase")
                            && dynamicMap.containsKey("operationType")
                            && dynamicMap.containsKey("argumentTuples")
                            && dynamicMap.containsKey("argumentTemplates")
                            && dynamicMap.containsKey("resultTuples")
                            && dynamicMap.containsKey("resultTemplates")) {
                        final Stream<T> argumentTuples = getDeserializer(tupleClass).listFromDynamicObject(dynamicMap.get("argumentTuples")).stream();
                        final Stream<TT> argumentTemplates = getDeserializer(templateClass).listFromDynamicObject(dynamicMap.get("argumentTemplates")).stream();
                        final OperationEvent.Invocation<T, TT> invocation = OperationEvent.invocation(tupleSpace, OperationType.valueOf((String) dynamicMap.get("operationType")), argumentTuples, argumentTemplates);
                        if(dynamicMap.get("operationPhase").equals(OperationPhase.INVOCATION.toString())) {
                            return invocation;
                        } else if(dynamicMap.get("operationPhase").equals(OperationPhase.COMPLETION.toString())) {
                            final Stream<T> resultTuples = getDeserializer(tupleClass).listFromDynamicObject(dynamicMap.get("resultTuples")).stream();
                            final Stream<TT> resultTemplates = getDeserializer(templateClass).listFromDynamicObject(dynamicMap.get("resultTemplates")).stream();
                            return invocation.toCompletion(resultTuples, resultTemplates);
                        }
                    }
                } else if (dynamicMap.get("eventType").equals(TupleEvent.class.getSimpleName())) {
                    if(dynamicMap.containsKey("before")
                            && dynamicMap.containsKey("effect")
                            && dynamicMap.containsKey("tuple")
                            && dynamicMap.containsKey("template")) {
                        final T tuple = Optional.ofNullable(dynamicMap.get("tuple")).map(t -> getDeserializer(tupleClass).fromDynamicObject(t)).orElse(null);
                        final TT template = Optional.ofNullable(dynamicMap.get("template")).map(t -> getDeserializer(templateClass).fromDynamicObject(t)).orElse(null);
                        return TupleEvent.of(tupleSpace, (Boolean) dynamicMap.get("before"), TupleEvent.Effect.valueOf((String) dynamicMap.get("effect")), tuple, template);
                    }
                }
            }
        }
        throw new IllegalArgumentException("Cannot read " + getSupportedMIMEType());
    }
}
