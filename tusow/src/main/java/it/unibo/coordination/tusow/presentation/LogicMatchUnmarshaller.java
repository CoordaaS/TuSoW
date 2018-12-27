package it.unibo.coordination.tusow.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.unibo.coordination.linda.logic.LogicMatch;
import it.unibo.coordination.linda.logic.LogicTemplate;
import it.unibo.coordination.linda.logic.LogicTuple;

import java.util.Map;

public class LogicMatchUnmarshaller extends DynamicUnmarshaller<LogicMatch> {

    public LogicMatchUnmarshaller(MIMETypes mimeType, ObjectMapper mapper) {
        super(LogicMatch.class, mimeType, mapper);
    }

    @Override
    public LogicMatch fromDynamicObject(Object dynamicObject) {
        if (dynamicObject instanceof Map) {
            final var dynamicMap = (Map<String, ?>) dynamicObject;

            if (dynamicMap.containsValue("template")) {
                final LogicTemplate template = getUnmarshaller(LogicTemplate.class).fromDynamicObject(dynamicMap.get("template"));
                final Object tupleObject;

                if ((tupleObject = dynamicMap.get("tuple")) != null) {
                    final LogicTuple tuple = getUnmarshaller(LogicTuple.class).fromDynamicObject(tupleObject);

                    return template.matchWith(tuple);
                }

                return LogicMatch.failed(template);
            }
        }
        throw new IllegalArgumentException("Cannot read " + getSupportedMIMEType());
    }
}
