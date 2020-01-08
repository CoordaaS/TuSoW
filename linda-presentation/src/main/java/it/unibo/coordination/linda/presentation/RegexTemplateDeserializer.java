package it.unibo.coordination.linda.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.unibo.coordination.linda.text.RegexTemplate;

import java.util.Map;

class RegexTemplateDeserializer extends DynamicDeserializer<RegexTemplate> {

    public RegexTemplateDeserializer(MIMETypes mimeType, ObjectMapper mapper) {
        super(RegexTemplate.class, mimeType, mapper);
    }

    @Override
    public RegexTemplate fromDynamicObject(Object dynamicObject) {
        if (dynamicObject instanceof Map) {
            Map<String, ?> map = (Map<String, ?>) dynamicObject;
            if (map.containsKey("template")) {
                Object tuple = map.get("template");
                if (tuple instanceof String) {
                    return RegexTemplate.of((String) tuple);
                }
            }
        }
        throw new IllegalArgumentException("Cannot read " + getSupportedMIMEType());
    }
}
