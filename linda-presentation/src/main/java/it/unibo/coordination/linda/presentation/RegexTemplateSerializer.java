package it.unibo.coordination.linda.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.unibo.coordination.linda.string.RegexTemplate;

import java.util.Map;

class RegexTemplateSerializer extends DynamicSerializer<RegexTemplate> {

    public RegexTemplateSerializer(MIMETypes mimeType, ObjectMapper mapper) {
        super(mimeType, mapper);
    }

    @Override
    public Object toDynamicObject(RegexTemplate object) {
        return Map.of("template", object.getTemplate().pattern());
    }
}
