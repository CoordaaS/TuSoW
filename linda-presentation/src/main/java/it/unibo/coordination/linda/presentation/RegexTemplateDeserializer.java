package it.unibo.coordination.linda.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.unibo.coordination.linda.string.RegexTemplate;

import java.util.Map;

class RegexTemplateDeserializer extends DynamicDeserializer<RegexTemplate> {

    public RegexTemplateDeserializer(MIMETypes mimeType, ObjectMapper mapper) {
        super(RegexTemplate.class, mimeType, mapper);
    }

    @Override
    public RegexTemplate fromDynamicObject(Object dynamicObject) {
        if (dynamicObject instanceof Map) {
            var map = (Map<String, ?>) dynamicObject;
            if (map.containsKey("template")) {
                var tuple = map.get("template");
                if (tuple instanceof String) {
                    return RegexTemplate.of((String) tuple);
                }
            }
        }
        throw new IllegalArgumentException("Cannot read " + getSupportedMIMEType());
    }
}
