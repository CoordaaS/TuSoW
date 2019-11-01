package it.unibo.coordination.linda.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.unibo.coordination.linda.text.StringTuple;

import java.util.Map;

class StringTupleDeserializer extends DynamicDeserializer<StringTuple> {

    public StringTupleDeserializer(MIMETypes mimeType, ObjectMapper mapper) {
        super(StringTuple.class, mimeType, mapper);
    }

    @Override
    public StringTuple fromDynamicObject(Object dynamicObject) {
        if (dynamicObject instanceof Map) {
            var map = (Map<String, ?>) dynamicObject;
            if (map.containsKey("tuple")) {
                var tuple = map.get("tuple");
                if (tuple instanceof String) {
                    return StringTuple.of((String) tuple);
                }
            }
        }
        throw new IllegalArgumentException("Cannot read " + getSupportedMIMEType());
    }
}
