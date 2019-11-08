package it.unibo.coordination.linda.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.unibo.coordination.linda.core.Template;
import it.unibo.coordination.linda.core.Tuple;
import it.unibo.coordination.linda.core.events.OperationEvent;
import it.unibo.coordination.linda.core.events.TupleEvent;
import it.unibo.coordination.linda.core.events.TupleSpaceEvent;

import java.util.HashMap;
import java.util.Optional;

class TupleSpaceEventSerializer<T extends Tuple<T>, TT extends Template<T>> extends DynamicSerializer<TupleSpaceEvent> {

    private Class<T> tupleClass;
    private Class<TT> templateClass;

    public TupleSpaceEventSerializer(MIMETypes mimeType, ObjectMapper mapper, Class<T> tupleClass, Class<TT> templateClass) {
        super(mimeType, mapper);
        this.tupleClass = tupleClass;
        this.templateClass = templateClass;
    }

    @Override
    public Object toDynamicObject(TupleSpaceEvent object) {
        final var tupleSpaceEventMap = new HashMap<String, Object>();
        if(object instanceof TupleEvent) {
            var tupleEvent = (TupleEvent<T, TT>) object;
            tupleSpaceEventMap.put("eventType", TupleEvent.class.getSimpleName());
            tupleSpaceEventMap.put("tupleSpace", tupleEvent.getTupleSpaceName());
            tupleSpaceEventMap.put("effect", tupleEvent.getEffect());
            tupleSpaceEventMap.put("before", tupleEvent.isBefore());
            tupleSpaceEventMap.put("tuple", Optional.ofNullable(tupleEvent.getTuple()).map(t -> getSerializer(tupleClass).toDynamicObject((T) t)).orElse(null));
            tupleSpaceEventMap.put("template", Optional.ofNullable(tupleEvent.getTemplate()).map(t -> getSerializer(templateClass).toDynamicObject((TT) t)).orElse(null));
        } else if(object instanceof OperationEvent) {
            var operationEvent = (OperationEvent<T, TT>) object;
            tupleSpaceEventMap.put("eventType", OperationEvent.class.getSimpleName());
            tupleSpaceEventMap.put("tupleSpace", operationEvent.getTupleSpaceName());
            tupleSpaceEventMap.put("operationType", operationEvent.getOperationType());
            tupleSpaceEventMap.put("operationPhase", operationEvent.getOperationPhase());
            tupleSpaceEventMap.put("argumentTuples", getSerializer(tupleClass).toDynamicObject(operationEvent.getArgumentTuples()));
            tupleSpaceEventMap.put("argumentTemplates", getSerializer(templateClass).toDynamicObject(operationEvent.getArgumentTemplates()));
            tupleSpaceEventMap.put("resultTuples", getSerializer(tupleClass).toDynamicObject(operationEvent.getResultTuples()));
            tupleSpaceEventMap.put("resultTemplates", getSerializer(templateClass).toDynamicObject(operationEvent.getResultTemplates()));
        } else {
            throw new IllegalArgumentException();
        }
        return tupleSpaceEventMap;
    }
}
