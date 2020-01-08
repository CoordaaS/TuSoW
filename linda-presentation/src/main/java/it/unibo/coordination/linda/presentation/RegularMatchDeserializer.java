package it.unibo.coordination.linda.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.unibo.coordination.linda.text.RegexTemplate;
import it.unibo.coordination.linda.text.RegularMatch;
import it.unibo.coordination.linda.text.StringTuple;

import java.util.Map;

class RegularMatchDeserializer extends DynamicDeserializer<RegularMatch> {

    public RegularMatchDeserializer(MIMETypes mimeType, ObjectMapper mapper) {
        super(RegularMatch.class, mimeType, mapper);
    }

    @Override
    public RegularMatch fromDynamicObject(Object dynamicObject) {
        if (dynamicObject instanceof Map) {
            final Map<String, ?> dynamicMap = (Map<String, ?>) dynamicObject;

            if (dynamicMap.get("template") instanceof String) {
                final RegexTemplate template = RegexTemplate.of((String) dynamicMap.get("template"));

                final Object tupleObject;
                if ((tupleObject = dynamicMap.get("tuple")) instanceof String) {
                    final StringTuple tuple = StringTuple.of((String) tupleObject);

                    return template.matchWith(tuple);
                }

                return RegularMatch.failed(template);
            }
        }
        throw new IllegalArgumentException("Cannot read " + getSupportedMIMEType());
    }
}
