package it.unibo.coordination.linda.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.unibo.coordination.linda.string.RegexTemplate;
import it.unibo.coordination.linda.string.RegularMatch;
import it.unibo.coordination.linda.string.StringTuple;

import java.util.Map;

class RegularMatchDeserializer extends DynamicDeserializer<RegularMatch> {

    public RegularMatchDeserializer(MIMETypes mimeType, ObjectMapper mapper) {
        super(RegularMatch.class, mimeType, mapper);
    }

    @Override
    public RegularMatch fromDynamicObject(Object dynamicObject) {
        if (dynamicObject instanceof Map) {
            final var dynamicMap = (Map<String, ?>) dynamicObject;

            if (dynamicMap.get("template") instanceof String) {
                final var template = RegexTemplate.of((String) dynamicMap.get("template"));

                final Object tupleObject;
                if ((tupleObject = dynamicMap.get("tuple")) instanceof String) {
                    final var tuple = StringTuple.of((String) tupleObject);

                    return template.matchWith(tuple);
                }

                return RegularMatch.failed(template);
            }
        }
        throw new IllegalArgumentException("Cannot read " + getSupportedMIMEType());
    }
}
